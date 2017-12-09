package com.hik.demo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

/**
 * @description 通过cifs/smb协议获取共享的网络文件
 * @createtime 2017年12月8日 下午2:04:14
 * @email cyhua_csu@163.com
 * @song 平凡之路
 */
public class GetFilesByCifs {

	/**
	 * 使用cifs协议获取nas文件
	 */
	public static SmbFile getFilesByCifs(String dirName) {
		String nasUrl = "smb://username:password@IP/";
		if(!dirName.equals("")){
			if(!dirName.endsWith("/")){
				nasUrl +=dirName+"/";
			}else{
				nasUrl +=dirName;
			}
		}
		SmbFile smbFile = null;
		try {
			smbFile = new SmbFile(nasUrl);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return smbFile;
	}

	/**
	 * 将nas文件复制到本地
	 */
	public static void downloadFileBySmb(SmbFile smbFile) {
		SmbFile[] smbFiles = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			smbFiles = smbFile.listFiles();
			for (SmbFile file : smbFiles) {
				if (file.isFile() && file.getName().equals("qqqqqqq_liu3.mp4")) {
					bis = new BufferedInputStream(new SmbFileInputStream(file));
					bos = new BufferedOutputStream(new FileOutputStream(new File("E:/nas_demo/cifs/qqqqqqq_liu3.mp4")));
					byte[] buffer = new byte[(int) file.length()];
					int read = 0;
					while ((read = bis.read(buffer)) != -1) {
						bos.write(buffer, 0, read);
					}
				} else {
					continue;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				bis.close();
				bos.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		}
	}

	/**
	 * 下载目录：即将某个目录及目录下的文件拷贝到本地
	 * 
	 * @param smbFile
	 *            SmbFile对象
	 * @param srcDirName
	 *            远程文件目录（即要下载的目录）
	 * @param desDirName
	 *            保存到本地的目录
	 */
	public static void downloadDirBySmb(SmbFile smbFile, String srcDirName,
			String desDirName) {
		SmbFile[] smbFiles;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			smbFiles = smbFile.listFiles();
			srcDirName += "/";
			for (SmbFile file : smbFiles) {
				String fName = file.getName();
				boolean isDir = file.isDirectory();
				if (isDir && fName.equals(srcDirName)) {
					desDirName += File.separator + srcDirName;
					File desFile = new File(desDirName);
					desFile.mkdir();
					SmbFile[] srcFiles = file.listFiles();
					for (SmbFile smbFile2 : srcFiles) {
						if (smbFile2.isDirectory()) {
							continue;
						} else {
							bis = new BufferedInputStream(new SmbFileInputStream(smbFile2));
							String fileName = desFile.getAbsolutePath()+ File.separator + smbFile2.getName();
							bos = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
							byte[] buffer = new byte[(int) smbFile2.length()];
							int read = 0;
							while ((read = bis.read(buffer)) != -1) {
								bos.write(buffer, 0, read);
							}
						}
					}
				} else {
					continue;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
				bis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 下载远程服务器上的所有文件
	 * 
	 * @param smbFileObj
	 *            SmbFile对象
	 * @param srcDir
	 *            远程目录
	 * @param desDir
	 *            本地保存目录
	 */
	public static String dirName ="";
	public static void downloadAllFiles(SmbFile smbFileObj, String srcDir,
			String desDir) {
		SmbFile[] smbFiles;
		// 如果源目录为空，则目标目录即为传入的值
		if (!srcDir.equals("") || srcDir != null) {
			desDir = removeFileSeparator(desDir);
			if (desDir.indexOf(removeFileSeparator(srcDir)) == 0 && !desDir.endsWith(removeFileSeparator(srcDir))) {
				desDir += "/" + srcDir;
			}
		}
		if (smbFileObj == null) {
			System.out.println("服务器连接失败，请检查原因！");
			return;
		}
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			smbFiles = smbFileObj.listFiles();
			for (SmbFile smbFile : smbFiles) {
				String smbFileName = smbFile.getName();
				if (!smbFile.isDirectory()) {
					bis = new BufferedInputStream(new SmbFileInputStream(smbFile));
					// 如果目标目录中含有文件分隔符，则创建文件时不加分隔符，否则加文件分隔符
					String desDirName = filterFileSeparator(desDir);
					bos = new BufferedOutputStream(new FileOutputStream(new File(desDirName + smbFileName)));
					byte[] buffer = new byte[(int) smbFile.length()];
					int read = 0;
					while ((read = bis.read(buffer)) != -1) {
						bos.write(buffer, 0, read);
					}
				} else {
					dirName += smbFileName;
					desDir = filterFileSeparator(desDir);
					desDir += smbFileName;
					File file = new File(desDir);
					if (!file.exists()) {
						file.mkdir();
					}
					SmbFile smbObj = getFilesByCifs(dirName);
					downloadAllFiles(smbObj, smbFileName, desDir);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 去掉重复的文件分隔符
	 * 
	 * @param dirName
	 * @return
	 */
	public static String filterFileSeparator(String dirName) {
		if (!dirName.endsWith("/") || !dirName.endsWith("\\")) {
			dirName += "/";
		}

		return dirName;
	}

	/**
	 * 删掉最后的文件分隔符
	 * 
	 * @param dirName
	 * @return
	 */
	public static String removeFileSeparator(String dirName) {
		if (dirName.endsWith("/")) {
			dirName = dirName.substring(0, dirName.lastIndexOf("/"));
		}
		if (dirName.endsWith("\\")) {
			dirName = dirName.substring(0, dirName.lastIndexOf("\\"));
		}
		return dirName;
	}

	/**
	 * 本地文件的复制
	 */
	public static void copyLocalFile() {
		File srcFile = new File(
				"");
		File desFile = new File("E:/nas_demo/cifs/lu2_test.mp4");
		FileInputStream fis = null;
		FileOutputStream fos = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			fis = new FileInputStream(srcFile);
			fos = new FileOutputStream(desFile);
			bis = new BufferedInputStream(fis);
			bos = new BufferedOutputStream(fos);
			byte[] outByte = new byte[(int) srcFile.length()];
			int read = 0;
			while ((read = bis.read(outByte)) != -1) {
				bos.write(outByte, 0, read);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				fis.close();
				fos.close();
				bis.close();
				bos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		String dirName = "";
		SmbFile smbFile = getFilesByCifs(dirName);
		// downloadFileBySmb(smbFile);
		// copyLocalFile();
		// downloadDirBySmb(smbFile, "331912524", "E:/nas_demo/cifs");
		downloadAllFiles(smbFile, "", "E:/nas_demo/cifs");
	}

}
