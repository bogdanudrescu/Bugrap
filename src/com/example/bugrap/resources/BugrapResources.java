package com.example.bugrap.resources;

import com.vaadin.server.ClassResource;

/**
 * Provide resources.
 * 
 * @author bogdan
 */
public class BugrapResources {

	/*
	 * The single instance.
	 */
	private static BugrapResources instance = new BugrapResources();

	/**
	 * Provide the resources instance.
	 * @return	the resources instance.
	 */
	public static BugrapResources getInstance() {
		return instance;
	}

	/**
	 * Provide the resource.
	 * @param resourceName	the name of the resource.
	 * @return	the resource object.
	 */
	public ClassResource getResource(String resourceName) {
		ClassResource classResource = new ClassResource(this.getClass(), resourceName);

		return classResource;
	}

}
