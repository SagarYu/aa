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
 * 1.ʹ��jpcapץȡ��������������,
 * 2.�����Ҫ������������,�����̷ֱ߳�ץȡ���������ϵ����������
 * 3.����ͼ��Ķ������������ȡ������
 * 4.��������Ϊ��ʵ��,�ڵ�һ�ε���ʱ����ץ�����߳�;
 * 5.Ŀǰû�����ֹͣץȡ����....
 * @author www.NetJava.cn
 */
public class Tcpdump {
   public static void main(String args[]){
	   //����ͳ���߳�
	   Tcpdump.ins();
	   //ģ��������ȡ�߳�
	   new Thread(
		new Runnable(){
			public void run(){
				while(true){
					try{
						Thread.sleep(3000);
					}catch(Exception ef){}
					System.out.println("***********ͳ�Ƶ�������************");
					System.out.println(Tcpdump.ins().getNameTrafficMap());;
				}
			}
		}
	   ).start();
   }
  
	
   /**
    * ��ʵ�����ã�����������������ķ���ʱ,����ͨ���������
    * ����,��֤������ͳ���̵߳�����,��ֻ������һ��
    * */
   public synchronized static Tcpdump ins(){
	   if(null==tcpdump){
		   tcpdump=new Tcpdump();
		   tcpdump.init();
	   }
	   return tcpdump;
	    
   }
   
   /**���ɱ����Servlet������������ͼ��������*/
	public   Map<String, Integer> getNameTrafficMap(){
		return nameTrafficMap;
	}
	
	/**
	 * ������������������ͳ���߳�
	 * ע�⣺���ص�ַ����127.0.0.1�ϵĲ�ͳ��
	 */
	private   void init()  {
		try{
			//��ȡ�����ϵ�����ӿڶ���
	 final	NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		for(int i=0;i<devices.length;i++){
			NetworkInterface nc=devices[i];
			//��������Ϊ��Ч����,��ץ������ַ.
			 if(nc.addresses.length>0){
				//һ�����������ж����ַ,ֻȡ��һ����ַ
			 String addr=nc.addresses[0].address.toString();
			 // ����ĳ�������ϵ�ץȡ����,
			 JpcapCaptor jpcap = JpcapCaptor.openDevice(nc, 2000, true, 100);
			 //������Ӧ��ץȡ�̲߳�����
			 LoopPacketThread lt=new LoopPacketThread(jpcap,addr);
			 lt.start();
			 System.out.println( addr+"�ϵĲɼ��̼߳�����************");
			 }
		  }
		}catch(Exception ef){
			ef.printStackTrace();
			System.out.println("start caputerThread error ! ! ! !"+ef);
		}

	}
	
	/**IP��ץ�����ĳ��ȷ���hash����,�ñ��г��ȼ������ĳ���*/
	    void putNetValue(String name,int value){
		if(nameTrafficMap.containsKey(name)){
		  value=nameTrafficMap.get(name)+value;
		  nameTrafficMap.put(name, value);
		}else{
			nameTrafficMap.put(name, value);
		}
	}
	
	
	
	
	
    private Tcpdump(){}
	/**����ĳ����ַ���ֺ�����ͳ��ֵ*/
	private   Map<String, Integer> nameTrafficMap=new java.util.HashMap();
	//��ʵ��
	private static Tcpdump tcpdump=null;
}
	

/**
 * ץ��������,ʵ��PacketReceiver�еķ���,�����ݰ�����ʱ����.
 * @author www.NetJava.cn
 */
class DumpPacket  implements PacketReceiver {
	  private String ipAdd;
	  
	  DumpPacket(String ipAdd){
		  this.ipAdd=ipAdd;
	  }
	  
	  //ʵ�ְ�ͳ��
	public void receivePacket(Packet packet) {
		//�����ݼ��뻺����д�ͼƬ����servlet��ȡ
		Tcpdump.ins().putNetValue(ipAdd, packet.len);
//		System.out.println(currentTime()+": "+ipAdd+"  �յ�����Ϊ:*"+ packet.len);
//		System.out.println(packet);
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
}

/**ͳ��ĳһ�����������ߵ��߳�*/
class LoopPacketThread extends Thread{
	 private JpcapCaptor jpcap ;
	 private String ipAdd;
	
	 /**
	  * ������
	  * @param jpcap:cap����
	  * @param ipAdd:�����ַ����
	  */
	public LoopPacketThread(JpcapCaptor jpcap,String ipAdd){
		this.jpcap=jpcap;
		 this.ipAdd=ipAdd;
	}
	
	public void run(){
		//-1��ʾ��Զץȡ
		this.jpcap.loopPacket(-1, new DumpPacket(this.ipAdd));
	}
}

//   /**�����ټ���һ���ڴ�����߳�*/
//class CheckMemory extends Thread{
//	
//	public void run(){
//		while(true){
//			try{
//				//�����ڴ�ռ����
//			long freeM=	java.lang.Runtime.getRuntime().freeMemory();
//			Long result=java.lang.Runtime.getRuntime().totalMemory()-freeM;
//			String rs=""+result/1000;
//			int is=Integer.parseInt(rs);
//			
//			Tcpdump.ins().putNetValue("�����ڴ�ռ��", is);
//			 
//				Thread.sleep(1000);
//			}catch(Exception ef){
//				
//			}
//		}
//	}
//	
//}
 
