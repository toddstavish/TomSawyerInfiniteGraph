package com.infinitegraph.tomsawyer;

import java.io.IOException;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.infinitegraph.Vertex;
import com.infinitegraph.Edge;
import com.infinitegraph.BaseEdge;
import com.infinitegraph.BaseVertex;
import com.infinitegraph.AccessMode;
import com.infinitegraph.Transaction;
import com.infinitegraph.GraphFactory;
import com.infinitegraph.GraphDatabase;
import com.infinitegraph.ConfigurationException;

import com.tomsawyer.model.TSModel;
import com.tomsawyer.model.TSModelElement;
import com.tomsawyer.model.defaultmodel.TSDefaultModel;
import com.tomsawyer.licensing.TSLicenseManager;
import com.tomsawyer.project.TSProject;
import com.tomsawyer.project.xml.TSProjectXMLReader;
import com.tomsawyer.view.drawing.swing.TSSwingDrawingView;
import com.tomsawyer.integrator.TSIntegratorException;
import com.tomsawyer.util.swing.TSTomSawyerApplications;


public class GraphVizApp extends JFrame {

	private static TSProject project;
	private static TSModel model;
	Database db = new Database();
    
    public GraphVizApp()
	{
		TSLicenseManager.setUserName(System.getProperty("user.name"));
		TSLicenseManager.initTSSLicensing();
	}
	
    public void loadProject(String filename) throws IOException
    {
        project = new TSProject();

        TSProjectXMLReader reader = new TSProjectXMLReader(filename);
        reader.setProject(this.project);
        reader.read();
    }

	public void initDB()
	{
		if (new File("Graph.CSV.boot").exists())
		{
			db.delete();
		}

		db.create();
		db.open();

		db.createData();
	}
    
    public void initModel()
    {
        model = new TSDefaultModel();
        project.getSchema(GraphVizConstants.MODULE_GRAPHVIZ).initModel(model);   
    }
    
    public void initDBAndModel()
	{
		initDB();
		initModel();
	}
	
    public void initGUI()
	{
		TSTomSawyerApplications.setLookAndFeel(this);
		TSTomSawyerApplications.setIcon(this);

		this.setSize(800, 600);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Graph Visualization Application");

		JTabbedPane tabbedPane = new JTabbedPane();

		TSSwingDrawingView view1 =
			(TSSwingDrawingView) this.project.newView(
				GraphVizConstants.MODULE_GRAPHVIZ,
				GraphVizConstants.GRAPHVIZ_VIEW_DRAWING);

		view1.setModel(model);
		tabbedPane.add("View 1", view1.getComponent());

		this.getContentPane().add(tabbedPane);
	}
	
	public static void addNode(String ID, String Property){
	    
	    TSModelElement elt1 = model.getModelIndex().getModelElement(
			GraphVizConstants.GRAPHVIZ_ELEMENT_TYPE_NODE,
			GraphVizConstants.GRAPHVIZ_NODE_ATTRIBUTE_ID,
			ID,
			true);

		if (elt1 == null)
		{
			elt1 = model.newModelElement(GraphVizConstants.GRAPHVIZ_ELEMENT_TYPE_NODE);
			elt1.setAttribute(GraphVizConstants.GRAPHVIZ_NODE_ATTRIBUTE_ID, ID);
			elt1.setAttribute(GraphVizConstants.GRAPHVIZ_NODE_ATTRIBUTE_PROPERTY, Property);
			model.addElement(elt1);
		}
	}
	
	/*
	public static void addNodeCustomer(String ID, String City, String DOB, String FamilyName, String GivenName, String SSN){
	    
	    TSModelElement elt1 = model.getModelIndex().getModelElement(
			GraphVizConstants.GRAPHVIZ_ELEMENT_TYPE_CUSTOMER,
			GraphVizConstants.GRAPHVIZ_CUSTOMER_ATTRIBUTE_ID,
			ID,
			true);

		if (elt1 == null)
		{
			elt1 = model.newModelElement(GraphVizConstants.GRAPHVIZ_ELEMENT_TYPE_CUSTOMER);
			elt1.setAttribute(GraphVizConstants.GRAPHVIZ_CUSTOMER_ATTRIBUTE_ID, ID);
			model.addElement(elt1);
		}
	}*/
	
	public static void addEdge(String ID, String TO, String FROM){
	    
		TSModelElement elt1 = model.getModelIndex().getModelElement(
			GraphVizConstants.GRAPHVIZ_ELEMENT_TYPE_EDGE,
			GraphVizConstants.GRAPHVIZ_EDGE_ATTRIBUTE_ID,
			ID,
			true);

		if (elt1 == null)
		{
			elt1 = model.newModelElement(GraphVizConstants.GRAPHVIZ_ELEMENT_TYPE_EDGE);
			elt1.setAttribute(GraphVizConstants.GRAPHVIZ_EDGE_ATTRIBUTE_ID, ID);
			elt1.setAttribute(GraphVizConstants.GRAPHVIZ_EDGE_ATTRIBUTE_TO, TO);
			elt1.setAttribute(GraphVizConstants.GRAPHVIZ_EDGE_ATTRIBUTE_FROM, FROM);
			model.addElement(elt1);
		}
	}
	
    public static void main(String[] args) throws Exception, IOException, TSIntegratorException {
        
        // Initialize Tom Sawyer
        GraphVizApp application = new GraphVizApp();
        application.loadProject("GraphViz.tsp");
    	application.initDBAndModel();
    	application.initGUI();
    	application.setVisible(true);
		
        // Create null transaction, null graph database instance
    	Transaction tx = null;
    	GraphDatabase graphDB = null;
    	
        // Extract graph
        try {
        	
            // Open graph database
            graphDB = GraphFactory.open("Graph.CSV", "config.properties");
        	
            // Start transaction
     		tx = graphDB.beginTransaction(AccessMode.READ);
     		
     		// Extract vertices
            for(Vertex vertex : graphDB.getVertices())
            {   
                String properties = new String();
                System.out.println(vertex.getPropertyNames());
                
                for(String property : vertex.getPropertyNames())
                {
                    if (!property.equals("ooObj") && !property.equals("connector"))
                    {
                        properties = properties + "," + "\"" + property + "\":" + vertex.getProperty(property);
                        String ID = String.valueOf(vertex.getId());
                        addNode(ID, vertex.getProperty(property).toString());
                    }
                }
                //if (vertex instanceof Airport)
                //String ID = String.valueOf(vertex.getId());
                //addNode(ID);
                //json.append("{\"an\":{\"" + vertex.getId() + "\":{\"label\":\"" + vertex.getId() + "\"" + properties + "}}}" + "\r\n");
            }
            
            // Extract edges
            for(Edge edge : graphDB.getEdges())
            {   
                String source = String.valueOf(edge.getOrigin().getId());
                String target = String.valueOf(edge.getTarget().getId());
                addEdge(String.valueOf(edge.getId()),source,target);
                //json.append("{\"ae\":{\"" + source + "_" + target + "\":{\"source\":\"" + source + "\",\"directed\":false,\"target\":\"" + target + "\"}}}" + "\r\n");
            }
            
     		// Commit transaction
     		tx.commit();
 		}
        catch (ConfigurationException cE)
        {
            System.out.println("> Configuration Exception was thrown ... ");
            System.out.println(cE.getMessage());
        }
 		finally
 		{
 			// If the transaction was not committed, complete
 			// will roll it back
 			if (tx != null)
 			{
 				tx.complete();
 			}
 			if (graphDB != null)
 			{
 				graphDB.close();
 			}
 		}
    }
  
}