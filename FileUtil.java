package com.dev2.intern.util;

import java.io.File;

public class FileUtil {
	
	public static final String FILE_DIRECTORY = "E:\\01. 인턴\\files\\";
	
    /**
	 * 저장할 Directory가 없을 경우 생성하기 위한 함수
	 */
	public static void checkExistDirectory() {
		File directory = new File(FILE_DIRECTORY);
		
		if (directory.exists() == false) {
			directory.mkdirs();
		}
	}
	
	public static boolean saveFile(MultipartFile file) {
		return false;
	}
	
	public static Time extractVideoDuration(String path) {
		URL url;
		Player player = null;
		
		try {
			url = new URL(path);
			player = Manager.createPlayer(url);
		} catch (MalformedURLException murle) {
			murle.printStackTrace();
		} catch (NoPlayerException ne) {
			ne.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return player.getDuration();
	}
	
	public static boolean deleteFile(String path) {
		return false;
	}
}
