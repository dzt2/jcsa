package com.jcsa.jcmutest.project.util;

import java.io.File;

import com.jcsa.jcparse.test.cmd.CCompiler;

/**
 * It provides interface to execute the commands for running the mutation test
 * project, which include the following operations that need to be implemented
 * according to the system on which the project is executed.
 * <br>
 * 	---------------------------------------------------------------------------<br>
 * 	1. <code>do_preprocess</code>: it applies the system compiler to translate
 * 	   a C source file into intermediate one in which macros like #include and
 *     #define would be automatically replaced.<br>
 *     <br>
 *  2. <code>do_compile</code>: it performs the compilation on the intermediate
 *     C source file linked with other library files to generate the executional
 *     program file (xxx.exe) as output.<br>
 *     <br>
 *  3. <code>do_execute</code>: it executes the shell script program generated
 *     from the test inputs according to the program it tries to execute, such
 *     that the related output be produced in specified output directory.<br>
 *  ---------------------------------------------------------------------------<br>
 *
 * @author yukimula
 *
 */
public interface MuCommandUtil {

	/* utility interfaces */
	/**
	 * It performs the pre-processing operations on .c file so to replace the
	 * macros like #include and #define, and generate intermediate code file
	 * as xxx.i, from which the AstTree and CirTree will be parsed, and will
	 * be directly used as the input of compilation to generate executional.
	 *
	 * @param compiler the system compiler used to perform pre-processing
	 * @param cfile the xxx.c source code file before being pre-processed
	 * @param ifile the xxx.i source code file after being pre-processed
	 * @param hdirs the directories in which the header files are linked
	 * @param parameters other parameters used for pre-processing command
	 * @throws Exception
	 */
	public boolean do_preprocess(CCompiler compiler, File cfile, File ifile,
			Iterable<File> hdirs, Iterable<String> parameters) throws Exception;
	/**
	 * It performs the compilation operation on .i files so to generate the
	 * executional file of xxx.exe, using the system compiler for compilation.
	 *
	 * @param compiler the system compiler used to perform pre-processing
	 * @param ifiles xxx.i after being pre-processed and directly used in compilation
	 * @param efile xxx.exe being generated as executional file after compilation
	 * @param hdirs the directories of the header files being linked for compilation
	 * @param lfiles the set of library files for being linked to generate executional
	 * @param params the other parameters used to complete the compilation.
	 * @throws Exception
	 */
	public boolean do_compile(CCompiler compiler, Iterable<File> ifiles, File efile,
			Iterable<File> hdirs, Iterable<File> lfiles, Iterable<String> params) throws Exception;
	/**
	 * It executes the shell script file for executing the test on program.
	 *
	 * @param shell_file the xxx.sh script file for executing
	 * @param exe_dir the directory in which the execution is performed.
	 * @throws Exception
	 */
	public boolean do_execute(File shell_file, File exe_dir) throws Exception;

	/* command-util-instances */
	/** the command-line utility for running commands on linux platform **/
	public static final MuCommandUtil linux_util = new LinuxCommandUtil();

}
