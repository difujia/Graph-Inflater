/*
* @(\#) RunInflater.java 1.1 22 April 2014
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
package inflater.runner;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.ParseException;
import org.apache.giraph.conf.GiraphConfiguration;
import org.apache.giraph.io.formats.GiraphFileInputFormat;
import org.apache.giraph.job.GiraphConfigurationValidator;
import org.apache.giraph.job.GiraphJob;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * Launch this using command with arguments: localInputPath localOutputPath. 
 * This runner will copy the original file into HDFS and copy the output to the specified localOuputPath
 * @author Fujia Di
 *
 */
public class RunInflater implements Tool {

	private Configuration	conf;
	private Path			inputHDFS;
	private Path			outputHDFS;

	@Override
	public Configuration getConf() {
		return conf;
	}

	@Override
	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	@Override
	public int run(String[] args) throws IOException, ClassNotFoundException, InterruptedException, ParseException {
		if (args.length < 2) {
			return -1;
		}
		if (conf == null) {
			conf = new Configuration();
		}
		
		GiraphConfiguration giraphConf = new GiraphConfiguration(getConf());
		giraphConf.addResource(new Path("giraph-site.xml"));

		GiraphJob job = new GiraphJob(giraphConf, giraphConf.getComputationName());
		
		Path inputLocal = new Path(args[0]);
		Path outputLocal = new Path(args[1]);

		// We copy file from local file system to HDFS
		FileSystem fs = FileSystem.get(giraphConf);
		inputHDFS = new Path(fs.getHomeDirectory(), "Giraph Source" + File.separator + inputLocal.getName() + File.separator + inputLocal.getName());
		inputHDFS = fs.makeQualified(inputHDFS);
		
		outputHDFS = new Path(fs.getHomeDirectory(), "Giraph Source" + File.separator + inputLocal.getName() + File.separator + "output");
		outputHDFS = fs.makeQualified(outputHDFS);
		
		fs.copyFromLocalFile(false, true, inputLocal, inputHDFS);

		// Delete output path because Hadoop cannot override it.
		if (fs.exists(outputHDFS)) fs.delete(outputHDFS, true);

		FileOutputFormat.setOutputPath(job.getInternalJob(), outputHDFS);
		GiraphFileInputFormat.addVertexInputPath(giraphConf, inputHDFS);
		new GiraphConfigurationValidator<>(giraphConf).validateConfiguration();
		boolean result = job.run(true);
		if (result) {
			fs.copyToLocalFile(false, new Path(outputHDFS, "part-m-00000"), outputLocal);			
		}
		return result ? 0 : -1;
	}

	public static void main(String[] args) throws Exception {
		System.exit(ToolRunner.run(new RunInflater(), args));
	}

}
