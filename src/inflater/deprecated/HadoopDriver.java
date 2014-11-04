/*
* @(\#) HadoopDriver.java 1.1 22 April 2014
* 
 * Copyright (\copyright) 2014 University of York & British Telecommunications plc
* This Software is granted under the MIT License (MIT)

*

* Copyright (c) <2014> <Team S4E>

*

* Permission is hereby granted, free of charge, to any person obtaining a copy

* of this software and associated documentation files (the "Software"), to deal

* in the Software without restriction, including without limitation the rights

* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell

* copies of the Software, and to permit persons to whom the Software is

* furnished to do so, subject to the following conditions:

*

* The above copyright notice and this permission notice shall be included in

* all copies or substantial portions of the Software.

*

* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR

* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,

* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE

* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER

* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,

* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN

* THE SOFTWARE.
*
*/
package inflater.deprecated;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HadoopDriver {

	private Path		outHDFS;
	private FileSystem	fs;
	private boolean		result	= false;

	/**
	 * Do a Hadoop work with the specified input file. This method will return
	 * when the work finishes.
	 * 
	 * @param src
	 * @param work
	 * @return True if the Hadoop work succeeded, false otherwise.
	 * @throws IOException
	 */
	public boolean doWork(File src, HadoopWork work) throws IOException {
		fs = FileSystem.get(work.getConf());

		Path inHDFS = new Path("Giraph Source/" + src.getName() + "/" + src.getName());
		Path inLocal = new Path(src.getAbsolutePath());
		fs.copyFromLocalFile(false, true, inLocal, inHDFS);

		// Delete output path if exists.
		outHDFS = new Path("Giraph Source/" + src.getName() + "/output");
		if (fs.exists(outHDFS)) fs.delete(outHDFS, true);
		try {
			System.out.println("Hadoop is about to start");
			result = work.run(inHDFS, outHDFS);
		} catch (ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
			result = false;
		}
		System.out.println("Hadoop result: " + result);
		return result;
	}

	/**
	 * @return True if the Hadoop work succeeded, false otherwise.
	 */
	public boolean getResult() {
		return result;
	}

	/**
	 * Copy output of current work from HDFS to local file system.
	 * 
	 * @param localPath
	 *            Target local path.
	 * @param delSrc
	 *            Whether delete the output in HDFS
	 * @throws IOException
	 */
	public void copyOutputToLocal(String localPath, boolean delSrc) throws IOException {
		if (!result)
			throw new IllegalStateException("Unable to copy output because Giraph work failed or not started.");

		Path target = new Path(localPath);
		
		// Because outHDFS is a directory.
		Path source = new Path(outHDFS, "part-m-00000");
		fs.copyToLocalFile(delSrc, source, target);
	}
	
	public File readOutput() {
		if (!result)
			throw new IllegalStateException("Unable to copy output because Giraph work failed or not started.");		
		// Because outHDFS is a directory.
		Path source = new Path(outHDFS, "part-m-00000");
		return new File(source.toString());
	}
/*
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
		InflaterWork work = new InflaterWork();
		HadoopDriver driver = new HadoopDriver();
		File input = new File("/Volumes/storage/Documents/in York/Work Space/GPMS-Team/input/130");
		boolean result = driver.doWork(input, work);
		if (result) {
			driver.copyOutputToLocal("/Volumes/storage/Documents/in York/Work Space/GPMS-Team/input", false);
		}
		System.exit(result ? 0 : -1);
	}
	*/
}
