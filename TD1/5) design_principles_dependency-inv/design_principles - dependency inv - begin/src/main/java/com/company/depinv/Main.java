package com.company.depinv;

import java.io.IOException;
import java.io.PrintWriter;

public class Main {

	public static void main(String[] args) throws IOException {
		
		Message msg = new Message("This is a message again");
		MessagePrinter printer = new MessagePrinter();
		Formatter formatter = new TextFormatter();
		PrintWriter writer = new PrintWriter("test_msg.txt");
		printer.writeMessage(msg, "test_msg.txt", formatter, writer);
		writer.close();
	}

}
