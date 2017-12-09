package com.hik.demo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * @description 通过ftp协议获取文件
 * @createtime 2017年12月8日 下午2:06:40
 * @email cyhua_csu@163.com
 * @song 平凡之路
 */
public class GetFileByFtp {

	public static void main(String[] args) {
		downLoadFile("331912524", "E:/nas_demo/ftp");
		 deleteLongFile("E:/nas_demo/ftp");
	}

	/**
	 * 获取fpt连接
	 * 
	 * @return 成功，返回true;失败，返回false
	 */
	private static FTPClient ftpClient;

	public static boolean getFtpConnection(String remoteFile) {
		boolean isConnect = false;
		ftpClient = new FTPClient();
		try {
			ftpClient.connect("ip", 21);
			ftpClient.login("username", "password");
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			int code = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(code)) {
				ftpClient.disconnect();
				isConnect = false;
			} else {
				isConnect = true;
				ftpClient.setControlEncoding("utf-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isConnect;
	}

	/**
	 * 下载文件
	 * 
	 * @param remoteFile
	 *            远程文件地址
	 * @param localPath
	 *            本地文件存储路径
	 */
	public static void downLoadFile(String remoteFile, String localPath) {
		boolean isConnnect = getFtpConnection(remoteFile);
		if (!isConnnect) {
			System.out.println("ftp连接失败");
			return;
		}

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			ftpClient.changeWorkingDirectory(remoteFile);
			FTPFile[] ftpFiles = ftpClient.listFiles();
			for (FTPFile ftpFile : ftpFiles) {
				if (ftpFile.isDirectory()) {
					String fileName = ftpFile.getName();
					File file = new File(localPath + File.separator + fileName);
					file.mkdir();
					downLoadFile(fileName, file.getAbsolutePath());
				}
			}
			for (FTPFile ftpFile : ftpFiles) {
				File localFile = new File(localPath + File.separator
						+ ftpFile.getName());
				fos = new FileOutputStream(localFile);
				bos = new BufferedOutputStream(fos);
				ftpClient.retrieveFile(ftpFile.getName(), bos);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
				// ftpClient.logout();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 递归删除文件
	 * 
	 * @param fileName
	 */
	public static void deleteLongFile(String fileName) {
		File file = new File(fileName);
		File[] files = file.listFiles();
		for (File file2 : files) {
			if (file2.isDirectory()) {
				String name = file2.getAbsolutePath();
				deleteLongFile(name);
			}
			boolean del = file2.delete();
			System.out.println(del);
		}
	}
}
