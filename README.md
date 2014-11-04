#Graph Inflater

A Hadoop/Giraph app that layouts shape-unaware graph (vertices and edges).

The layout algorithm is an implementation of the Fruchterman Algorithm with some improvements including the concept of gravity and randomised initial layout.

>Original paper of the algorithm: ftp://132.180.22.143/axel/papers/reingold:graph_drawing_by_force_directed_placement.pdf

The app reads json formatted graph data line by line from a plain text file. Each line in the file represents a single vertex. The following sample data is indented for readability, but it should be written inline in the input file.

##Sample input format:

```JSON
{"id":1,
	"values":{"coordinate":{"x":392.2290344238281,"y":-653.1674194335938,"z":0.0},"weight":0.0},
	"edges":[
		{"targetId":2,"weight":0.0},
		{"targetId":3,"weight":0.0},
		{"targetId":4,"weight":0.0},
		{"targetId":5,"weight":0.0},
		{"targetId":6,"weight":0.0},
		{"targetId":7,"weight":0.0},
		{"targetId":8,"weight":0.0}]
}
```

Note that the coordinate values will be ignored as they are read.

For simplicity, the output format is exactly the same as the input. After the app finishes running, new coordinate values will be inserted into each vertex (each line).

##Dependencies
The app was developed and tested under hadoop 2.2 and giraph 1.1 . They are *not* included in the repository, please download them from their sites.

Apart from hadoop and giraph, this app uses Apache Common-Lang 2.5 for `Object.equals`, `Object.hashCode` and `Object.toString` implementations, Gson 2.2.4 for json parsing and Mockito 1.9.5 for unit testing. They are *included* in the repository.
