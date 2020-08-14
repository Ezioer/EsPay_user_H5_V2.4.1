package com.easou.androidsdk.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * 工具类，处理I/O操作
 * 
 * @author ted
 * 
 */
public class FileHelper {

	private FileHelper() {
	}

	/**
	 * 从文件中获取字符串
	 * 
	 * @param file
	 * @return the file content
	 */
	public static String readFile(File file) {
		if (!initFile(file))
			return "";
		InputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			return "";

		}
		return inputStream2String(fis);
	}

	/**
	 * seperate the file content by special divider
	 * 
	 * @param file
	 * @param divider
	 * @return the sections of the file
	 */
	public static String[] seperate(File file, String divider) {
		String data = readFile(file);
		return data.split(divider);
	}

	/**
	 * convert input stream to string by the special encode.
	 * 
	 * @param InputStream
	 * @return String
	 */
	public static String inputStream2String(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len = -1;
		byte[] data = new byte[2048];
		String result = null;
		try {
			while ((len = is.read(data)) != -1) {
				baos.write(data, 0, len);
			}
			result = new String(baos.toByteArray());
		} catch (IOException e) {
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
			}
		}
		return result;
	}

	/**
	 * write the msg to the file
	 * @param file
	 * @param msg
	 */
	public static void writeFile(File file, String msg) {
		if (!initFile(file))
			return;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(msg.getBytes());
		} catch (IOException e) {
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
			}
		}

	}

	/**
	 * delete the specify file
	 * 
	 * @param file
	 * @return success return true otherwise false
	 */
	public static void delFiles(File... files) {
		for (File file : files) {
			if (file != null && file.exists()) {
				file.delete();
			}
		}
	}

	/**
	 * init file if the file is not exists
	 * 
	 * @param file
	 * @return if the file is null || createFile failed return false,else return
	 *         true
	 */
	public static boolean initFile(File file) {
		if (file == null) {
			return false;
		}
		if (!file.exists()) {
			File dir = file.getParentFile();
			if (!dir.exists()) {

				if (dir.mkdirs()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						return false;
					}
				} else
					return false;
			}
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Store the state if success return true else return false
	 * 
	 * @return void
	 */
	public static void writeObject(Object obj, ObjectOutputStream oos) {
		try {
			oos.writeObject(obj);
			oos.flush();
		} catch (IOException e) {
		} finally {
			try {
				if (oos != null)
					oos.close();
			} catch (IOException ioe) {
			}

		}
	}

	/**
	 * Restore the state if success return true else return false
	 * 
	 * @return Object
	 */
	public static Object readObject(ObjectInputStream ois) {
		Object obj = null;
		try {
			obj = ois.readObject();
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		} finally {
			try {
				if (ois != null)
					ois.close();
			} catch (IOException ioe) {
			}
		}
		return obj;
	}
	
	/**
	 * write obj to the special file
	 * @param obj
	 * @param file
	 */
	public static void writeObject(Object obj, File file) {
		if(file == null || obj == null)
			return ;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			writeObject(obj, oos);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}finally{
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
			}
		}
		
	}

		
	/**
	 * read object from special File
	 * @param file
	 * @return if the file or the obj not exists then return null
	 */
	public static Object readObject(File file) {
		Object object = null;
		if (file == null)
			return null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			object = readObject(ois);
		} catch (IOException e) {
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
			}
		}
		return object;
	}

	/**
	 * delete the file or directory by file name equal to "rm -f"
	 * 
	 * @param String
	 *            fileName
	 * @return boolean
	 */
	public static boolean delFile(String fileName) {
		File tempFile = new File(fileName);
		boolean isDel = false;
		if (tempFile.isFile()) {
			if (tempFile.exists()) {
				isDel = tempFile.delete();
			}
		} else if (tempFile.isDirectory()) {
			if (tempFile.exists()) {
				File[] dirs = tempFile.listFiles();
				for (File file : dirs) {
					file.delete();
				}
				isDel = tempFile.delete();
			}
		}
		return isDel;
	}

}
