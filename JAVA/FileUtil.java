package com.dev2.intern.util;

import java.io.File;

/**
 *
 * @author MJYoun
 * @since 2017. 03. 06.
 *
 */
/**
 * static method안에서는 @Value나 @Autowired된 값을 사용할 수 없다.
 * 따라서 이 소스에서는 properties안에 있는 fileDirectory의 값을
 * 호출하는 service로 부터 받아서 진행한다.
 */
public class FileUtil {

		private static void createDirectory(String fileDirectory) {
			File directory = new File(fileDirectory);

			if (directory.exists() == false) {
				directory.mkdirs();
			}
		}

		public static String saveFile(MultipartFile multipartFile, String fileDirectory) throws IllegalStateException, IOException {
			createDirectory(fileDirectory);

			String storedFileName = fileDirectory + UuidUtil.createUuidWithoutHyphen();
			File file = new File(storedFileName);
			multipartFile.transferTo(file);

			return storedFileName;
		}

		public static boolean deleteFile(String location) {
			File file = new File(location);

			return file.delete();
		}
}
