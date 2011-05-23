package com.github.elyast.guids.core.internal;

import static org.junit.Assert.*;
import com.github.elyast.guids.core.internal.ServiceInjector;

import org.junit.Before;
import org.junit.Test;

public class ServiceInjectorTest {

	private ServiceInjector testObj;

	@Before
	public void setup() {
		testObj = new ServiceInjector();
	}
	
	@Test
	public void test() {
		testObj.stop();
	}

}
