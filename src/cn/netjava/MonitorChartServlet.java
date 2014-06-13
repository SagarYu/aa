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

/**�ṩ˳��ʱ��ͼ���Servlet*/
public class MonitorChartServlet extends HttpServlet {
     /**����ӿ���������:����ͼ�������еĸ���*/
	private static String[] netNamearray;
	 /** ��������:��Ӧ����ʷֵ�б�*/
    private  static Map<String,LinkedList<Integer>> nameValueOfList=new HashMap();
	/**ͼ�����չʾ��ʱ������:�������ʱ����������*/
	  private static final int timeLen=15;
	/**����ʱ���ߵĶ���*/
	private static java.util.LinkedList<String> timeList=new java.util.LinkedList();
	 
  
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try{
		  response.setHeader("Cache-Control", "no-cache");
		 response.setContentType("image/png");
		 java.io.OutputStream ous=response.getOutputStream();
         //����һ�����ݼ�����
		DefaultCategoryDataset data= getCategoryDataset();
	    JFreeChart	chart =ChartFactory.createLineChart("��������ʵʱ���@NetJava.cn","ʱ��(ff:ss)","�� ��",data,PlotOrientation.VERTICAL,true,false,false);
	    //����һ��:)
	    processChart(chart);
	    ChartUtilities.writeChartAsPNG(ous, chart,800, 300);
	    ous.flush();
	    ous.close();
	    System.out.println("ͼƬ������....");
		}catch(Exception ef){ef.printStackTrace();}
}

 
	/**
	 * ����barͼ�����ݼ�:
	 * @return:������Barͼ�����ݼ�
	 * ������ôϸ��,�������ȫͬ��.
	 */
	public   synchronized DefaultCategoryDataset getCategoryDataset(){
	   DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	    //����������map�е�key,��IP��תΪ����
	    Map<String,Integer> netMap= Tcpdump.ins().getNameTrafficMap();
	    System.out.println("---������ȡ����------: "+netMap.toString());
	    Set<String> ss= netMap.keySet();
	    String[] forTest=new String[ss.size()];
		   netNamearray=  ss.toArray(forTest);
		   System.out.println("---��������------: "+netNamearray.length);
	    //���������þ����������ݵĽṹ
	    for(int i=0;i<netNamearray.length;i++){
	    	if(!nameValueOfList.containsKey(netNamearray[i])){
	    	nameValueOfList.put(netNamearray[i], new LinkedList());
	    	System.out.println("---���-----------: "+netNamearray[i]);
	    	}
	    }
	    System.out.println("---���Servlet��ʼ��OK,�����������-----------: "+netNamearray.length);
         //�������ϵļ�������ÿ��������Ӧ���б�
	   for(int i=0;i<netNamearray.length;i++){
		   String key=netNamearray[i];
		 int currnetValue=  netMap.get(key);
         //		�����������map��ԭ��������
  	     netMap.put(key, 0);
		 LinkedList historyValueList=nameValueOfList.get(key);
		 //��ʷ���ݱ���û�г���timeLen��ʱ,�����б�ͷ��
		 if(historyValueList.size()<timeLen){
			 historyValueList.addFirst(currnetValue);
		 }
		 else{
			 historyValueList.removeLast();
			 historyValueList.addFirst(currnetValue);
		 }
	   }
        //�鿴�Ƿ�Ҫ���ݱ����������ʱ����:
	   if(timeList.size()<timeLen){
		   //���뵽ǰ��
		   timeList.addFirst(currentTime());
	   }else{
		   //�Ƴ������ʱ��
		   timeList.removeLast();
          // ���뵽ǰ��
		   timeList.addFirst(currentTime());
	   }
	   //��������䵽dataset��
           for (int i = 0; i <  netNamearray.length; i++) {
        	   LinkedList<Integer> historyValueList=nameValueOfList.get(netNamearray[i]);
        	   for (int series = 0; series <timeList.size(); series ++) {
         		   //ĳ�����������г��Ȳ���,Ҫ��ȫ,������Ϊ0
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
	 *��־ʱ����Ϣ
	 * @return����־����ʱ��
	 */
	private static String currentTime(){
		Date d = new Date();
		SimpleDateFormat kk=new SimpleDateFormat("mm:ss");
		String strTime=kk.format(d); 
		return strTime;
		
	}
	 
	public void init() throws ServletException {
	}
	
	//�����ɵ���ͼ�������ݵķ���
	public void processChart( JFreeChart jfc) {
		try {
            //�������ı�ʾ��
			LineAndShapeRenderer renderer = new LineAndShapeRenderer(); 
			//�۵�Ŀɼ���
			renderer.setShapesVisible(true);
			//�۵��Ƿ�Ϊʵ��
			//renderer.setShapesFilled(false);
			 //������ϸ
			renderer.setStroke(new BasicStroke(1f));
			renderer.setItemLabelsVisible(true);
			renderer.setBaseItemLabelsVisible(true);
			//��ͼ�ϵ������Ƿ�ɼ���
			renderer.setSeriesItemLabelGenerator(1,new StandardCategoryItemLabelGenerator());
			renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
            //ȫͼ����� Ĭ��Ϊtrue  ���Ϊfalse��Ȼ�ߺܶ�
	          jfc.setAntiAlias(true);
	         //  ֻ�ر�����Ŀ���ݹ��� 
	         //���ñ�������
	         jfc.getTitle().setFont(new Font("����Ҧ��",Font.BOLD,15));
	         //����ʾ��ͼ���ִ�С****
	         jfc.getLegend().setItemFont(new Font("����",Font.LAYOUT_NO_LIMIT_CONTEXT,12));
	         //����ʾ��ͼ������ɫ****
	         jfc.getLegend().setBackgroundPaint(Color.GREEN);
	         // ͼ����ɫ
             jfc.setBackgroundPaint(Color.WHITE);
			    CategoryPlot plot = (CategoryPlot)jfc.getPlot();
			    // �������ı���͸���ȣ�0.0��1.0��,��ɫ�ľ��޹���
                plot.setBackgroundAlpha(0.8f);
                //����X������������ɫ
                plot.setRangeGridlinePaint(Color.orange);
                //����X������������ɫ
                plot.setDomainGridlinePaint(new Color(124, 100, 100));
                //����X�����������ʴ�
                 plot.setRangeGridlineStroke(new BasicStroke(0.2f));
                //����Y�����������ʴ�
                plot.setDomainGridlineStroke(new BasicStroke(0.1f));
                //�������������������ı�ʾ��
			    plot.setRenderer(renderer);
		        CategoryAxis domainAxis = plot.getDomainAxis(); 
		        domainAxis.setAxisLineVisible(true);
		        //����x���������
		        domainAxis.setLabelFont(new Font("����", Font.LAYOUT_NO_LIMIT_CONTEXT, 13));
		        //X�������С **************
		        domainAxis.setTickLabelFont(new Font("����", Font.LAYOUT_NO_LIMIT_CONTEXT, 10));
		        plot.setDomainAxis(domainAxis); 
                  ValueAxis rangeAxis = plot.getRangeAxis();
                 // rangeAxis.setAutoRangeMinimumSize(500); 
                 
//                  rangeAxis.setPositiveArrowVisible(true);
//                  rangeAxis.setAutoRange(true);
//                  rangeAxis.setLowerBound(1000);
//                  rangeAxis.setFixedAutoRange(500);
		        //������ߵ�һ�� Item ��ͼƬ���˵ľ��� 
		        rangeAxis.setUpperMargin(0.4);
		        //������͵�һ�� Item ��ͼƬ�׶˵ľ��� 
		        rangeAxis.setLowerMargin(0.2); 
		        //����y���������
		        rangeAxis.setLabelFont(new Font("����",Font.LAYOUT_NO_LIMIT_CONTEXT , 13));
		        //Y�������С **********
		        rangeAxis.setTickLabelFont(new Font("����", Font.LAYOUT_NO_LIMIT_CONTEXT, 13));
		       
		        plot.setRangeAxis(rangeAxis); 
               // ��������͸���� 
		        plot.setForegroundAlpha(1f); 
				plot.setDomainGridlinesVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
