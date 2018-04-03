package com.duali.itouchpop2_test.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

public class FileHelper {
	private static final String FLAG = "/data/duali/FLAG";
	private static final String TAG = FileHelper.class.getSimpleName();


	public static File makeDirectory(String dir_path){
		File dir = new File(dir_path);
		if (!dir.exists())
		{
			dir.mkdirs();
			Log.i( TAG , "!dir.exists" );
		}else{
			Log.i( TAG , "dir.exists" );
		}

		return dir;
	}

	/**
	 * 1111 1111
	 * @param dir
	 * @return file 
	 */
	public static  File makeFile(File dir , String file_path){
		File file = null;
		boolean isSuccess = false;
		if(dir.isDirectory()){
			file = new File(file_path);
			if(file!=null&&!file.exists()){
				Log.i( TAG , "!file.exists" );
				try {
					isSuccess = file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
					Log.i(TAG, "111ϻ111 1111 = " + isSuccess);
				}
			}else{
				Log.i( TAG , "file.exists" );
			}
		}
		return file;
	}

	/**
	 * (dir/file) 1111 111 111111
	 * @param file
	 * @return String
	 */
	public static  String getAbsolutePath(File file){
		return ""+file.getAbsolutePath();
	}

	/**
	 * (dir/file) 1111 1ϱ1
	 * @param file
	 */
	public static  boolean deleteFile(File file){
		boolean result;
		if(file!=null&&file.exists()){
			file.delete();
			result = true;
		}else{
			result = false;
		}
		return result;
	}

	/**
	 * 111Ͽ111 üũ 1ϱ1
	 * @param file
	 * @return
	 */
	public static  boolean isFile(File file){
		boolean result;
		if(file!=null&&file.exists()&&file.isFile()){
			result=true;
		}else{
			result=false;
		}
		return result;
	}

	/**
	 * 111丮 1111 üũ 1ϱ1
	 * @param dir
	 * @return
	 */
	public static  boolean isDirectory(File dir){
		boolean result;
		if(dir!=null&&dir.isDirectory()){
			result=true;
		}else{
			result=false;
		}
		return result;
	}

	/**
	 * 1111 1111 1111 Ȯ11 1ϱ1
	 * @param file
	 * @return
	 */
	public static  boolean isFileExist(File file){
		boolean result;
		if(file!=null&&file.exists()){
			result=true;
		}else{
			result=false;
		}
		return result;
	}

	/**
	 * 1111 1̸1 1ٲٱ1
	 * @param file
	 */
	public static  boolean reNameFile(File file , File new_name){
		boolean result;
		if(file!=null&&file.exists()&&file.renameTo(new_name)){
			result=true;
		}else{
			result=false;
		}
		return result;
	}

	/**
	 * 111丮11 1ȿ1 111111 1111 1ش1.
	 * @param file
	 * @return
	 */
	public static  String[] getList(File dir){
		if(dir!=null&&dir.exists())
			return dir.list();
		return null;
	}

	/**
	 * 111Ͽ1 1111 1111
	 * @param file
	 * @param file_content
	 * @return
	 */
	public static  boolean writeFile(File file , byte[] file_content){
		boolean result;
		FileOutputStream fos;
		if(file!=null&&file.exists()&&file_content!=null){
			try {
				fos = new FileOutputStream(file);
				try {
					fos.write(file_content);
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			result = true;
		}else{
			result = false;
		}
		return result;
	}

	/**
	 * 1111 1о1 1111
	 * @param file
	 */
	public static String readFile(File file){
		int readcount=0;
		byte[] buffer = null;
		if(file!=null&&file.exists()){
			try {
				FileInputStream fis = new FileInputStream(file);
				readcount = (int)file.length();
				buffer = new byte[readcount];
				fis.read(buffer);
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new String(buffer).trim();
	}

	/**
	 * 1111 1111
	 * @param file
	 * @param save_file
	 * @return
	 */
	public static  boolean copyFile(File file , String save_file){
		boolean result;
		if(file!=null&&file.exists()){
			try {
				FileInputStream fis = new FileInputStream(file);
				FileOutputStream newfos = new FileOutputStream(save_file);
				int readcount=0;
				byte[] buffer = new byte[1024];
				while((readcount = fis.read(buffer,0,1024))!= -1){
					newfos.write(buffer,0,readcount);
				}
				newfos.close();
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			result = true;
		}else{
			result = false;
		}
		return result;
	}
	
	public static boolean useWifi() {
		//Flag Set
		try{
			File ipSetFile = new File(FLAG);
			if(!ipSetFile.exists())
				createFile(ipSetFile);
			FileOutputStream fos = new FileOutputStream(ipSetFile);
			fos.write("W".getBytes());
			fos.getFD().sync();
			fos.close();
			chmod(FLAG);
			return true;
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private static boolean createFile(File mOutFile) throws IOException {
		if (mOutFile.exists()) {
			mOutFile.delete();
		}

		if (mOutFile.createNewFile()) {			
			return true;
		}
		return false;
	}

	public static boolean chmod(String filename) {
		try {
			String[] Cmd = {"chmod",  "666", filename};
			Runtime r = Runtime.getRuntime();
			Process p;
			p = r.exec(Cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			in.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
