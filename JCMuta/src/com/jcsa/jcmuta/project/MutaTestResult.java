package com.jcsa.jcmuta.project;

import java.io.File;
import java.io.FileInputStream;

import com.jcsa.jcmuta.mutant.code2mutation.MutationCodeType;
import com.jcsa.jcparse.lang.base.BitSequence;

/**
 * The test result contains compilation and execution results as:
 * (0) #type: Coverage|Weakness|Stronger
 * (1) #compile: true|false
 * (2) #output: [score-bit-sequence]
 * @author yukimula
 *
 */
public class MutaTestResult {
	
	private Mutant mutant;
	private MutationCodeType type;
	private boolean compile_passed;
	private BitSequence test_result;
	
	public MutaTestResult(Mutant mutant, boolean compiled_passed, 
			MutationCodeType type, BitSequence test_result) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else {
			this.mutant = mutant; this.type = type;
			this.compile_passed = compiled_passed;
			this.test_result = test_result;
		}
	}
	protected MutaTestResult(Mutant mutant, boolean compile_passed, 
			MutationCodeType type, File output, File output2) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else if(output == null || !output.isDirectory())
			throw new IllegalArgumentException("Invalid output: null");
		else if(output2 == null || !output2.isDirectory())
			throw new IllegalArgumentException("Invalid output2: null");
		else {
			this.mutant = mutant; this.type = type;
			this.compile_passed = compile_passed;
			
			if(this.compile_passed) 
				test_result = this.generate_test_result(output, output2);
			else this.test_result = null;
		}
	}
	private BitSequence generate_test_result(File output, File output2) throws Exception {
		int length = output.listFiles().length;
		BitSequence sequence = new BitSequence(length);
		
		for(int k = 0; k < length; k++) {
			File source_file = new File(output.getAbsolutePath() + File.separator + k);
			File target_file = new File(output2.getAbsolutePath() + File.separator + k);
			if(!this.compare_files(source_file, target_file)) { sequence.set(k, true); }
		}
		
		return sequence;
	}
	/**
	 * 
	 * @param source
	 * @param target
	 * @return true if two files are IDENTICAL
	 * @throws Exception
	 */
	private boolean compare_files(File source, File target) throws Exception {
		if(!source.exists() && !target.exists()) return true;
		else if(!source.exists() || !target.exists()) return false;
		else {
			FileInputStream source_stream = new FileInputStream(source);
			FileInputStream target_stream = new FileInputStream(target);
			
			boolean result = true; int len1, len2;
			byte[] source_buffer = new byte[1024];
			byte[] target_buffer = new byte[1024];
			while(result) {
				len1 = source_stream.read(source_buffer);
				len2 = source_stream.read(target_buffer);
				
				if(len1 != len2) result = false; 
				else if(len1 == -1 || len2 == -1) break;
				else {
					for(int k = 0; k < len1; k++) {
						if(source_buffer[k] != target_buffer[k]) {
							result = false; break;
						}
					}
				}
			}
			
			source_stream.close(); target_stream.close(); return result;
		}
	}
	
	public Mutant get_mutant() { return this.mutant; }
	public MutationCodeType get_type() { return this.type; }
	public boolean is_compile_passed() { return this.compile_passed; }
	public BitSequence get_test_result() { return this.test_result; }
	
	private static final StringBuilder buffer = new StringBuilder();
	@Override
	public String toString() {
		buffer.setLength(0);
		
		buffer.append("#mutant: " + mutant.get_id()).append("\n");
		buffer.append("#type: " + type).append("\n");
		buffer.append("#compile: " + this.compile_passed).append("\n");
		if(test_result != null)
			buffer.append("#output: " + test_result.toString()).append("\n");
		
		return buffer.toString();
	}
	
}
