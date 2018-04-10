package com.fangming.mq.activeMq.samples.explorerJms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

	// Simple example shows how a command line spring application can execute an
	// injected bean service. Also demonstrates how you can use @Value to inject
	// command line args ('--name=whatever') or application properties

	@Autowired
	private QueueMonitorService queueMonitorService;

	@Override
	public void run(String... args) {
		if (args.length > 0 && args[0].equals("exitcode")) {
			throw new ExitException();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
