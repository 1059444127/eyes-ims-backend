package com.tongren.service;

import com.tongren.bean.Constant;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 读写配置文件的服务
 * Created by ken on 2017/8/18.
 */
@Service
public class PropertyService {

	private static String rootPath = PropertyService.class.getResource("/").getPath();

	/**
	 * 读取一个键值对
	 * @param filePath
	 * @param key
	 */
	public String readString(String filePath, String key) {

		String targetPath = rootPath  + filePath;

		InputStream fis = null;

		try {

			fis = new FileInputStream(targetPath);
			Properties props = new Properties();
			props.load(fis);

			String value = props.getProperty(key);
			return value;
		} catch (IOException e) {

			System.err.println("属性文件读取错误");
			return null;
		} finally {

			//关闭输入流
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 读取一个键值对
	 * @param filePath
	 * @param key
	 */
	public Integer readInteger(String filePath, String key) {

		String targetPath = rootPath  + filePath;

		InputStream fis = null;

		try {

			fis = new FileInputStream(targetPath);
			Properties props = new Properties();
			props.load(fis);

			return Integer.parseInt(props.getProperty(key));
		} catch (IOException e) {

			System.err.println("属性文件读取错误");
			return null;
		} finally {

			//关闭输入流
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	/**
	 * 读取一组字符串键值对
	 * @param filePath
	 * @param keySet
	 * @return
	 */
	public Map<String, String> readStrings(String filePath, Set<String> keySet) {

		String targetPath = rootPath  + filePath;

		InputStream fis = null;

		try {

			fis = new FileInputStream(targetPath);
			Properties props = new Properties();
			props.load(fis);

			Map<String, String> map = new HashMap<>();
			for(String key : keySet)
				map.put(key, props.getProperty(key));

			return map;
		} catch (IOException e) {

			System.err.println("属性文件读取错误");
			return null;
		} finally {

			//关闭输入流
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 读取一组整形键值对
	 * @param filePath
	 * @param keySet
	 * @return
	 */
	public Map<String, Integer> readIntegers(String filePath, Set<String> keySet) {

		String targetPath = rootPath  + filePath;

		InputStream fis = null;

		try {

			fis = new FileInputStream(targetPath);
			Properties props = new Properties();
			props.load(fis);

			Map<String, Integer> map = new HashMap<>();
			for(String key : keySet)
				map.put(key, Integer.parseInt(props.getProperty(key)));

			return map;
		} catch (IOException e) {

			System.err.println("属性文件读取错误");
			return null;
		} finally {

			//关闭输入流
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	/**
	 * 更新一个键值对
	 * @param filePath
	 * @param key
	 * @param value
	 */
	public Integer update(String filePath, String key,String value) {

		String targetPath = rootPath  + filePath;

		OutputStream fos = null;
		InputStream fis = null;

		try {

			fis = new FileInputStream(targetPath);
			Properties props = new Properties();
			props.load(fis);

			fos = new FileOutputStream(targetPath);
			props.setProperty(key, value);
			// 将此 Properties 表中的属性列表（键和元素对）写入输出流
			props.store(fos, "");

			return Constant.CRUD_SUCCESS;

		} catch (IOException e) {

			System.err.println("属性文件更新错误");
			return Constant.CRUD_FAILURE;

		} finally {

			//关闭输入 输出流
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 更新一组键值对
	 * @param filePath
	 * @param map
	 */
	public Integer update(String filePath, Map<String, Object> map) {

		String targetPath = rootPath  + filePath;
		OutputStream fos = null;
		InputStream fis = null;
		try {

			fis = new FileInputStream(targetPath);
			Properties props = new Properties();
			props.load(fis);

			fos = new FileOutputStream(targetPath);
			for(Map.Entry<String, Object> entry : map.entrySet()) {

				String key = entry.getKey();
				String value = String.valueOf(entry.getValue());

				props.setProperty(key, value);
			}

			props.store(fos, ""); // 将此 Properties 表中的属性列表（键和元素对）写入输出流
			return Constant.CRUD_SUCCESS;

		} catch (IOException e) {

			System.err.println("属性文件更新错误");
			return Constant.CRUD_FAILURE;

		} finally {

			//关闭输入 输出流
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
