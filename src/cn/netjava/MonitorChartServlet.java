package cn.netjava;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**提供顺序时间图表的Servlet*/
public class MonitorChartServlet extends HttpServlet {
     /**网络接口名字数组:生成图表中序列的个数*/
	private static String[] netNamearray;
	 /** 网络名字:对应的历史值列表*/
    private  static Map<String,LinkedList<Integer>> nameValueOfList=new HashMap();
	/**图表上最长展示的时间点个数:超过则从时间表后面移走*/
	  private static final int timeLen=15;
	/**保存时间线的队列*/
	private static java.util.LinkedList<String> timeList=new java.util.LinkedList();
	 
  
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try{
		  response.setHeader("Cache-Control", "no-cache");
		 response.setContentType("image/png");
		 java.io.OutputStream ous=response.getOutputStream();
         //创建一个数据集对象：
		DefaultCategoryDataset data= getCategoryDataset();
	    JFreeChart	chart =ChartFactory.createLineChart("网络流量实时监控@NetJava.cn","时间(ff:ss)","数 量",data,PlotOrientation.VERTICAL,true,false,false);
	    //美化一下:)
	    processChart(chart);
	    ChartUtilities.writeChartAsPNG(ous, chart,800, 300);
	    ous.flush();
	    ous.close();
	    System.out.println("图片输出完成....");
		}catch(Exception ef){ef.printStackTrace();}
}

 
	/**
	 * 生成bar图的数据集:
	 * @return:可用于Bar图的数据集
	 * 不想那么细了,这个方法全同步.
	 */
	public   synchronized DefaultCategoryDataset getCategoryDataset(){
	   DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	    //将网络流量map中的key,即IP名转为数组
	    Map<String,Integer> netMap= Tcpdump.ins().getNameTrafficMap();
	    System.out.println("---本次提取数据------: "+netMap.toString());
	    Set<String> ss= netMap.keySet();
	    String[] forTest=new String[ss.size()];
		   netNamearray=  ss.toArray(forTest);
		   System.out.println("---网卡个数------: "+netNamearray.length);
	    //初化化放置具体流量数据的结构
	    for(int i=0;i<netNamearray.length;i++){
	    	if(!nameValueOfList.containsKey(netNamearray[i])){
	    	nameValueOfList.put(netNamearray[i], new LinkedList());
	    	System.out.println("---监控-----------: "+netNamearray[i]);
	    	}
	    }
	    System.out.println("---监控Servlet初始化OK,监控网卡个数-----------: "+netNamearray.length);
         //将网卡上的计数加入每个卡名对应的列表
	   for(int i=0;i<netNamearray.length;i++){
		   String key=netNamearray[i];
		 int currnetValue=  netMap.get(key);
         //		清空网络流量map中原来的数据
  	     netMap.put(key, 0);
		 LinkedList historyValueList=nameValueOfList.get(key);
		 //历史数据表中没有超过timeLen个时,加入列表头中
		 if(historyValueList.size()<timeLen){
			 historyValueList.addFirst(currnetValue);
		 }
		 else{
			 historyValueList.removeLast();
			 historyValueList.addFirst(currnetValue);
		 }
	   }
        //查看是否要根据本次请求更新时间线:
	   if(timeList.size()<timeLen){
		   //加入到前面
		   timeList.addFirst(currentTime());
	   }else{
		   //移除后面的时间
		   timeList.removeLast();
          // 加入到前面
		   timeList.addFirst(currentTime());
	   }
	   //将数据填充到dataset中
           for (int i = 0; i <  netNamearray.length; i++) {
        	   LinkedList<Integer> historyValueList=nameValueOfList.get(netNamearray[i]);
        	   for (int series = 0; series <timeList.size(); series ++) {
         		   //某个序列数据列长度不够,要补全,本处补为0
        		   if(historyValueList.size()==series){
        				   historyValueList.addFirst(0);
        		   }
        	    int value=historyValueList.get(series);
        	   //  value=new java.util.Random().nextInt(300)+100;
             dataset.addValue(value, netNamearray[i], timeList.get(series));
          }
       }
	return dataset;
	}
	/**
	 *日志时间信息
	 * @return：日志内容时间
	 */
	private static String currentTime(){
		Date d = new Date();
		SimpleDateFormat kk=new SimpleDateFormat("mm:ss");
		String strTime=kk.format(d); 
		return strTime;
		
	}
	 
	public void init() throws ServletException {
	}
	
	//对生成的线图进行美容的方法
	public void processChart( JFreeChart jfc) {
		try {
            //数据区的表示者
			LineAndShapeRenderer renderer = new LineAndShapeRenderer(); 
			//折点的可见性
			renderer.setShapesVisible(true);
			//折点是否为实体
			//renderer.setShapesFilled(false);
			 //线条粗细
			renderer.setStroke(new BasicStroke(1f));
			renderer.setItemLabelsVisible(true);
			renderer.setBaseItemLabelsVisible(true);
			//线图上的数据是否可见？
			renderer.setSeriesItemLabelGenerator(1,new StandardCategoryItemLabelGenerator());
			renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
            //全图抗锯齿 默认为true  如果为false锐度会高很多
	          jfc.setAntiAlias(true);
	         //  只关闭字体的抗锯齿功能 
	         //设置标题字体
	         jfc.getTitle().setFont(new Font("方正姚体",Font.BOLD,15));
	         //设置示例图文字大小****
	         jfc.getLegend().setItemFont(new Font("宋体",Font.LAYOUT_NO_LIMIT_CONTEXT,12));
	         //设置示例图背景颜色****
	         jfc.getLegend().setBackgroundPaint(Color.GREEN);
	         // 图表背景色
             jfc.setBackgroundPaint(Color.WHITE);
			    CategoryPlot plot = (CategoryPlot)jfc.getPlot();
			    // 数据区的背景透明度（0.0～1.0）,白色的就无关了
                plot.setBackgroundAlpha(0.8f);
                //数据X轴网格线条颜色
                plot.setRangeGridlinePaint(Color.orange);
                //数据X轴网格线条颜色
                plot.setDomainGridlinePaint(new Color(124, 100, 100));
                //数据X轴网格线条笔触
                 plot.setRangeGridlineStroke(new BasicStroke(0.2f));
                //数据Y轴网格线条笔触
                plot.setDomainGridlineStroke(new BasicStroke(0.1f));
                //给数据区增加数据区的表示者
			    plot.setRenderer(renderer);
		        CategoryAxis domainAxis = plot.getDomainAxis(); 
		        domainAxis.setAxisLineVisible(true);
		        //设置x轴标题字体
		        domainAxis.setLabelFont(new Font("宋体", Font.LAYOUT_NO_LIMIT_CONTEXT, 13));
		        //X轴字体大小 **************
		        domainAxis.setTickLabelFont(new Font("宋体", Font.LAYOUT_NO_LIMIT_CONTEXT, 10));
		        plot.setDomainAxis(domainAxis); 
                  ValueAxis rangeAxis = plot.getRangeAxis();
                 // rangeAxis.setAutoRangeMinimumSize(500); 
                 
//                  rangeAxis.setPositiveArrowVisible(true);
//                  rangeAxis.setAutoRange(true);
//                  rangeAxis.setLowerBound(1000);
//                  rangeAxis.setFixedAutoRange(500);
		        //设置最高的一个 Item 与图片顶端的距离 
		        rangeAxis.setUpperMargin(0.4);
		        //设置最低的一个 Item 与图片底端的距离 
		        rangeAxis.setLowerMargin(0.2); 
		        //设置y轴标题字体
		        rangeAxis.setLabelFont(new Font("宋体",Font.LAYOUT_NO_LIMIT_CONTEXT , 13));
		        //Y轴字体大小 **********
		        rangeAxis.setTickLabelFont(new Font("宋体", Font.LAYOUT_NO_LIMIT_CONTEXT, 13));
		       
		        plot.setRangeAxis(rangeAxis); 
               // 设置柱的透明度 
		        plot.setForegroundAlpha(1f); 
				plot.setDomainGridlinesVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
