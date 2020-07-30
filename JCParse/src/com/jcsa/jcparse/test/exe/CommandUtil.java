package com.jcsa.jcparse.test.exe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

import com.jcsa.jcparse.lang.ClangStandard;

/**
 * It provides interfaces to execute the command-line programs for 
 * completing the pre-processing, compilation and executing shell.
 * 
 * @author yukimula
 *
 */
public interface CommandUtil {
	
	/** the command-line interfaces to implement over Linux platform **/
	public static final CommandUtil linux_util = new LinuxCommandUtil();
	
	/**
	 * generate the intermediate code file by pre-processing the xxx.c source code file.
	 * @param compiler the compiler used to pre-process the .c file into .i file without #include or #define macros.
	 * @param cfile the xxx.c source code file to be pre-processed.
	 * @param ifile the xxx.i intermediate code file generated from pre-processing.
	 * @param hdirs the set of directories where xxx.h files are included.
	 * @param mfiles the files being used to generate macros {-imacros}
	 * @return true if the pre-processing succeeds.
	 * @throws Exception is thrown during executing the command-line program.
	 */
	public boolean do_preprocess(CCompiler compiler, File cfile, 
			File ifile, Iterable<File> hdirs, Iterable<File> mfiles) throws Exception;
	
	/**
	 * perform the compilation on the xxx.i files and generate the executional file of xxx.exe
	 * @param compiler the compiler used to compile the xxx.i files into xxx.exe file with provided parameters.
	 * @param ifiles the intermediate code file directly used for compilation
	 * @param efile the executional file of xxx.exe
	 * @param hdirs the set of directories where xxx.h files are included and used in compilation.
	 * @param lfiles the set of library files used to link and generate the executional programs.
	 * @param params the other parameters used in compiling the C programs.
	 * @return true if the executional files are successfully generated.
	 * @throws Exception is thrown during executing the command-line program.
	 */
	public boolean do_compile(CCompiler compiler, Iterable<File> ifiles, File efile, 
			Iterable<File> hdirs, Iterable<File> lfiles, Iterable<String> params) throws Exception;
	
	/**
	 * generate the instrumental code file from xxx.i file using the internal parser provided by JCParse
	 * @param ifile the xxx.i file after pre-processing and being parsed from C-parser.
	 * @param sfile the xxx.s file with instrumental methods to be inserted into the .c file.
	 * @param rfile the xxx.r file in which the instrumental analysis result being preserved in.
	 * @param c_template_file the template file of cruntime.txt used to parse the xxx.i file.
	 * @param standard the language standard used to parse the xxx.i source code file.
	 * @return true if the instrumental code file xxx.s is successfully generated.
	 * @throws Exception is thrown during executing the command-line program.
	 */
	public boolean do_instrument(File ifile, File sfile, 
			File rfile, File c_template_file, ClangStandard standard) throws Exception;
	
	/**
	 * generate the shell script file (xxx.sh) with given executional file.
	 * @param cdir the directory where the shell script is executed
	 * @param efile the xxx.exe (executional program file)
	 * @param inputs the test inputs used to generate command-line for running the program.
	 * @param odir the directory where the output information is preserved.
	 * @param timeout the maximal seconds needed for running the program on each test inputs or non-positive when time is not limited.
	 * @param sfile the xxx.sh shell script file being generated
	 * @return true if the shell file is successfully generated.
	 * @throws Exception is thrown during executing the command-line program.
	 */
	public boolean gen_normal_test_shell(File cdir, File efile, 
			Iterable<TestInput> inputs, File odir, long timeout, File sfile) throws Exception;
	
	/**
	 * generate the shell script file (xxx.sh) for running the instrumental program.
	 * @param cdir the directory where the shell script is executed
	 * @param efile the executional file (xxx.exe)
	 * @param inputs the set of test inputs used to run the program.
	 * @param rfile the file to preserve the instrumental analysis result
	 * @param odir the directory where the output and instrumental results are saved.
	 * @param timeout the maximal seconds needed for running the program on each test inputs or non-positive when time is not limited.
	 * @param sfile the xxx.sh shell script file being generated
	 * @return true if the shell file is successfully generated.
	 * @throws Exception is thrown during executing the command-line program.
	 */
	public boolean gen_instrumental_shell(File cdir, File efile, Iterable<TestInput> inputs, 
			File rfile, File odir, long timeout, File sfile) throws Exception;
	
	/**
	 * perform the execution on the shell script file as given
	 * @param sfile xxx.sh shell script file.
	 * @param cdir the directory under which the shell file is executed
	 * @return true when the shell script file is successfully completed.
	 * @throws Exception is thrown during executing the command-line program.
	 */
	public boolean do_execute_shell(File sfile, File cdir) throws Exception;
	
	/**
	 * copy the source file to the target file
	 * @param source to be copied to the target
	 * @param target the file being cloned with text.
	 * @throws Exception
	 */
	public static void copy_file(File source, File target) throws Exception {
		FileInputStream in = new FileInputStream(source);
		FileOutputStream ou = new FileOutputStream(target);
		
		byte[] buffer = new byte[1024 * 1024 * 16]; int length;
		while((length = in.read(buffer)) >= 0) {
			ou.write(buffer, 0, length);
		}
		
		in.close(); ou.close();
	}
	
	/**
	 * write the text to the specified file.
	 * @param file the specified file being written with text
	 * @param text the text being written to the specified file
	 * @throws Exception
	 */
	public static void write_text(File file, String text) throws Exception {
		FileWriter writer = new FileWriter(file);
		writer.write(text); writer.close();
	}
	
	/**
	 * read the text in the specified file
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String read_text(File file) throws Exception {
		FileReader reader = new FileReader(file);
		StringBuilder buffer = new StringBuilder();
		
		char[] buff = new char[1024 * 1024]; int length;
		while((length = reader.read(buff)) >= 0) {
			buffer.append(buff, 0, length);
		}
		
		reader.close(); return buffer.toString();
	}
	
	/**
	 * delete all the files under the directory and itself.
	 * @param file
	 * @throws Exception
	 */
	public static void delete_file(File file) throws Exception {
		if(file != null && file.exists()) {
			if(file.isDirectory()) {
				File[] ifiles = file.listFiles();
				for(File ifile : ifiles) {
					delete_file(ifile);
				}
			}
			file.delete();
			while(file.exists());
		}
	}
	
	/**
	 * @param dir the directory being created
	 * @throws Exception
	 */
	public static void make_directory(File dir) throws Exception {
		if(!dir.exists()) {
			dir.mkdir();
			while(!dir.exists());
		}
	}
	
	/**
	 * delete all the files under the directory
	 * @param dir
	 * @throws Exception
	 */
	public static void delete_files_in(File dir) throws Exception {
		File[] files = dir.listFiles();
		if(files != null) {
			for(File file : files) {
				delete_file(file);
			}
		}
	}
	
}
