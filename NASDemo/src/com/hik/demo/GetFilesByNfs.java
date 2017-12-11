package com.hik.demo;

import com.sun.xfile.XFile;

/**
 * @description 使用NFS协议下载
 * @createtime 2017年12月11日 上午10:13:17
 * @email cyhua_csu@163.com
 * @song 平凡之路
 */
public class GetFilesByNfs {

	private String url;
	private XFile xfile;
	
	/**
	 * 获取NFS连接
	 * @param ip
	 * @param dir
	 */
	public void getNfsConnection(String ip,String dir){
		if(!dir.equals("")){
			url = "nfs://"+ip+"/"+dir;
		}else{
			url = "nfs://"+ip;
		}
		xfile = new XFile(url);
		if(xfile.exists()){
			System.out.println("连接成功");
		}else{
			System.out.println("连接失败");
			return;//如果不return，就会一直尝试连接
		}
	}
	
	
	
	public static void main(String[] args) {
		GetFilesByNfs gfb = new GetFilesByNfs();
		gfb.getNfsConnection("10.192.77.13","331912524");
	}
}
