/*
* @(\#) InflaterWork.java 1.1 22 April 2014
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

import inflater.computation.SimpleComputation;
import inflater.computation.SimpleMasterCompute;
import inflater.formats.JsonCustomVertexInputFormat;
import inflater.formats.JsonCustomVertexOutputFormat;

import java.io.IOException;

import org.apache.giraph.conf.GiraphConfiguration;
import org.apache.giraph.io.formats.GiraphFileInputFormat;
import org.apache.giraph.job.GiraphConfigurationValidator;
import org.apache.giraph.job.GiraphJob;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class InflaterWork implements HadoopWork {

	private final GiraphConfiguration	giraphConf;

	public InflaterWork() throws IOException, ClassNotFoundException, InterruptedException {
		giraphConf = new GiraphConfiguration();
		giraphConf.addResource(new Path("giraph-site.xml"));

		giraphConf.setComputationClass(SimpleComputation.class);
		giraphConf.setMasterComputeClass(SimpleMasterCompute.class);
		giraphConf.setVertexInputFormatClass(JsonCustomVertexInputFormat.class);
		giraphConf.setVertexOutputFormatClass(JsonCustomVertexOutputFormat.class);
	}

	@Override
	public Configuration getConf() {
		return giraphConf;
	}

	@Override
	public boolean run(Path input, Path output) throws IOException, ClassNotFoundException, InterruptedException {
		GiraphJob testJob = new GiraphJob(giraphConf, getClass().getName());
		/*
		 * Input path must be set as either VertexInputPath or EdgeInputPath.
		 * Setting normal hadoop input path takes no effects.
		 */
		GiraphFileInputFormat.addVertexInputPath(giraphConf, input);

		/*
		 * Set output path using standard hadoop facility
		 */
		FileOutputFormat.setOutputPath(testJob.getInternalJob(), output);

		new GiraphConfigurationValidator<>(testJob.getConfiguration()).validateConfiguration();
		boolean result = testJob.run(true);
		return result;
	}
}
