package com.infinitegraph.tomsawyer;

import com.infinitegraph.csv.Load1;
import com.infinitegraph.csv.Load2;
import com.infinitegraph.csv.Load3;

import com.infinitegraph.Vertex;
import com.infinitegraph.Edge;
import com.infinitegraph.BaseEdge;
import com.infinitegraph.BaseVertex;
import com.infinitegraph.AccessMode;
import com.infinitegraph.Transaction;
import com.infinitegraph.GraphFactory;
import com.infinitegraph.GraphDatabase;
import com.infinitegraph.ConfigurationException;

public class Database
{

	public GraphDatabase getDb()
	{
		return db;
	}

	GraphDatabase db = null;

	String dbName = "Graph.CSV";
	String propFile = "config.properties";

	public void create()
	{
		try
		{
			GraphFactory.create(dbName, propFile);
		}
		catch (ConfigurationException e)
		{
			e.printStackTrace();
		}
	}

	public void delete()
	{
		try
		{
			GraphFactory.delete(dbName, propFile);
		}
		catch (ConfigurationException e)
		{
			e.printStackTrace();
		}
	}


	public void open()
	{
		try
		{
			db = GraphFactory.open(dbName, propFile);
		}
		catch (ConfigurationException e)
		{
			e.printStackTrace();
		}
	}

	public void close()
	{
		if (db != null)
		{
			db.close();
		}
	}

	public void createData()
	{
	    Load1 loader1 = new Load1(dbName, propFile);
		loader1.ingest();
		Load2 loader2 = new Load2(dbName, propFile);
		loader2.ingest();
		Load3 loader3 = new Load3(dbName, propFile);
		loader3.ingest();	
	}

}
