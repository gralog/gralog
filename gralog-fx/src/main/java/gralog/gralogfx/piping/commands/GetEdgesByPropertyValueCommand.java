// package gralog.gralogfx.piping;
// import gralog.structure.*;
// import gralog.rendering.*;
// import java.util.Set;



// public class GetEdgesByPropertyValueCommand extends CommandForGralogToExecute {
	

// 	// String propertyString;
//  //    String valueString;
//     // String neighbourString;



// 	public GetEdgesByPropertyValueCommand(String[] externalCommandSegments,Structure structure) {
// 		this.externalCommandSegments = externalCommandSegments;
//         this.structure = structure;

//         try {    
//             this.sourceId = Integer.parseInt(externalCommandSegments[2]);
//         }catch(NumberFormatException e) {
//             this.error = e;
//             this.fail();
//             return;
//         }

//         this.sourceVertex = this.structure.getVertexById(this.sourceId);

//         if (this.sourceVertex == null) {
//             this.fail();
//             this.error = new Exception("error: source vertex does not exist");
//             return;
//         }
// 	}


	

// 	public void handle() {

//         // int changeId;
       
//         //todo: this!!
        

//         // Set<Edge> conncetedEdges = this.sourceVertex.getOutgoingEdges();

//         // String edgeString = "";
//         // for (Edge e : conncetedEdges) {
//         //     edgeString = edgeString + "("+Integer.toString(e.getSource().getId())+","+Integer.toString(e.getTarget().getId())+")"+ "#";
//         // }
//         // if (edgeString.length() > 0 && null != edgeString) {
//         //     edgeString = edgeString.substring(0,edgeString.length()-1);
//         // }


//         this.setResponse(edgeString);

//         return;


//         // return v;
// 	}

// }
