package cn.netjava;
import java.text.SimpleDateFormat;
import java.util.*;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.PacketReceiver;
import jpcap.packet.DatalinkPacket;
import jpcap.packet.Packet;


/**
 * 1.使用jpcap抓取网络流量的主类,
 * 2.这个类要根据网卡个数,启动线程分别抓取各个网卡上的流量入表中
 * 3.生成图表的对象从流量表中取出数据
 * 4.这个类设计为单实例,在第一次调用时启动抓数据线程;
 * 5.目前没有设计停止抓取机制....
 * @author www.NetJava.cn
 */
public class Tcpdump {
   public static void main(String args[]){
	   //启动统计线程
	   Tcpdump.ins();
	   //模拟数据提取线程
	   new Thread(
		new Runnable(){
			public void run(){
				while(true){
					try{
						Thread.sleep(3000);
					}catch(Exception ef){}
					System.out.println("***********统计到的数据************");
					System.out.println(Tcpdump.ins().getNameTrafficMap());;
				}
			}
		}
	   ).start();
   }
  
	
   /**
    * 单实例调用：其它对象调用这个类的方法时,必须通过这个方法
    * 这样,保证了流量统计线程的启动,且只启动了一次
    * */
   public synchronized static Tcpdump ins(){
	   if(null==tcpdump){
		   tcpdump=new Tcpdump();
		   tcpdump.init();
	   }
	   return tcpdump;
	    
   }
   
   /**生成报表的Servlet调用用于生成图表中数据*/
	public   Map<String, Integer> getNameTrafficMap(){
		return nameTrafficMap;
	}
	
	/**
	 * 根据网卡个数，启动统计线程
	 * 注意：本地地址，即127.0.0.1上的不统计
	 */
	private   void init()  {
		try{
			//获取本机上的网络接口对象
	 final	NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		for(int i=0;i<devices.length;i++){
			NetworkInterface nc=devices[i];
			//大与零则为有效网卡,不抓本机地址.
			 if(nc.addresses.length>0){
				//一个网卡可能有多个地址,只取第一个地址
			 String addr=nc.addresses[0].address.toString();
			 // 创建某个卡口上的抓取对象,
			 JpcapCaptor jpcap = JpcapCaptor.openDevice(nc, 2000, true, 100);
			 //创建对应的抓取线程并启动
			 LoopPacketThread lt=new LoopPacketThread(jpcap,addr);
			 lt.start();
			 System.out.println( addr+"上的采集线程己启动************");
			 }
		  }
		}catch(Exception ef){
			ef.printStackTrace();
			System.out.println("start caputerThread error ! ! ! !"+ef);
		}

	}
	
	/**IP和抓到包的长度放入hash表中,用表中长度加入放入的长度*/
	    void putNetValue(String name,int value){
		if(nameTrafficMap.containsKey(name)){
		  value=nameTrafficMap.get(name)+value;
		  nameTrafficMap.put(name, value);
		}else{
			nameTrafficMap.put(name, value);
		}
	}
	
	
	
	
	
    private Tcpdump(){}
	/**存入某个地址名字和流量统计值*/
	private   Map<String, Integer> nameTrafficMap=new java.util.HashMap();
	//单实例
	private static Tcpdump tcpdump=null;
}
	

/**
 * 抓包监听器,实现PacketReceiver中的方法,当数据包到达时计数.
 * @author www.NetJava.cn
 */
class DumpPacket  implements PacketReceiver {
	  private String ipAdd;
	  
	  DumpPacket(String ipAdd){
		  this.ipAdd=ipAdd;
	  }
	  
	  //实现包统计
	public void receivePacket(Packet packet) {
		//将数据加入缓存表中待图片生成servlet提取
		Tcpdump.ins().putNetValue(ipAdd, packet.len);
//		System.out.println(currentTime()+": "+ipAdd+"  收到长度为:*"+ packet.len);
//		System.out.println(packet);
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
}

/**统计某一个网卡上流线的线程*/
class LoopPacketThread extends Thread{
	 private JpcapCaptor jpcap ;
	 private String ipAdd;
	
	 /**
	  * 构造器
	  * @param jpcap:cap对象
	  * @param ipAdd:网络地址名字
	  */
	public LoopPacketThread(JpcapCaptor jpcap,String ipAdd){
		this.jpcap=jpcap;
		 this.ipAdd=ipAdd;
	}
	
	public void run(){
		//-1表示永远抓取
		this.jpcap.loopPacket(-1, new DumpPacket(this.ipAdd));
	}
}

//   /**测试再加上一个内存监视线程*/
//class CheckMemory extends Thread{
//	
//	public void run(){
//		while(true){
//			try{
//				//计算内存占用量
//			long freeM=	java.lang.Runtime.getRuntime().freeMemory();
//			Long result=java.lang.Runtime.getRuntime().totalMemory()-freeM;
//			String rs=""+result/1000;
//			int is=Integer.parseInt(rs);
//			
//			Tcpdump.ins().putNetValue("这是内存占用", is);
//			 
//				Thread.sleep(1000);
//			}catch(Exception ef){
//				
//			}
//		}
//	}
//	
//}
 
