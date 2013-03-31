package gprover;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;

public class jgex_IO {
	public static String getWorkingDirectory() {
		return "/Users/kutach/Documents/Workspace/JGEX_SagaciousMatterFork/rules/";
	}
	
	public static Document openXMLfile(String path) {
		//path = "/Users/kutach/Documents/Workspace/JGEX_SagaciousMatterFork/xml_examples/6_GDD_FULL/81-109/100.gex.xml";

		try {
			//Setting system property for DOMImplementationRegistry 
			//System.setProperty(DOMImplementationRegistry.PROPERTY, "org.apache.xerces.dom.DOMImplementationSourceImpl");

			//Creating a DOMImplementationRegistry 
			DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();

			//Creating a DOMImplementation object
			DOMImplementationLS domImplLS = (DOMImplementationLS)registry.getDOMImplementation("LS");

			//Creating an LSParser object
			LSParser parser = domImplLS.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, "http://www.w3.org/2001/XMLSchema");

			//Obtaining a DOMConfiguration object 
			DOMConfiguration config = parser.getDomConfig();

			//Setting the error handler
			DOMErrorHandler errorHandler = new DOMErrorHandler() { public boolean handleError(DOMError error) { 
				System.out.println("Error Message:" + error.getMessage());
				return (error.getSeverity() == DOMError.SEVERITY_WARNING); } };
			config.setParameter("error-handler", errorHandler);

			//Setting schema validation parameters 
			config.setParameter("validate", Boolean.TRUE); 
			config.setParameter("schema-type", "http://www.w3.org/2001/XMLSchema");
			config.setParameter("validate-if-schema", Boolean.TRUE); 
			//config.setParameter("schema-location", "catalog.xsd");
			config.setParameter("charset-overrides-xml-encoding", false);

			FileInputStream inStream = null;
			InputStreamReader inReader = null;
			LSInput in = null;
			Document document = null;
			
			String sEncoding = "UNICODE";
			boolean bKeepTrying = true;
			
			while (bKeepTrying) {
				try {
					inStream = new FileInputStream(path);
					inReader = new InputStreamReader(inStream, sEncoding);
					in = domImplLS.createLSInput();
					in.setEncoding(sEncoding);
					in.setCharacterStream(inReader);
	
					//Parse the XML document 
					//Document document = parser.parseURI(path);
					document = parser.parse(in);
					bKeepTrying = false;
				} catch (LSException e) { 
					if (inReader != null)
						inReader.close();
					if (inStream != null)
						inStream.close();
					
					// System.out.println("LSException " + e.getMessage());
					if (sEncoding.equals("UNICODE"))
						sEncoding = "UTF-8";
					else if (sEncoding.equals("UTF-8"))
						sEncoding = "UTF-16";
					else if (sEncoding.equals("UTF-16"))
						sEncoding = "UTF-16LE";
					else
						bKeepTrying = false;
				}
			}
			
			if (inReader != null)
				inReader.close();
			if (inStream != null)
				inStream.close();
			if (document != null)
				System.out.println("XML document loaded");					
			
			return document;
			
		} catch (FileNotFoundException e) { 
			System.out.println("FileNotFound " + e.getMessage());
		} catch (UnsupportedEncodingException e) { 
			System.out.println("UnsupportedEncoding " + e.getMessage());
		} catch (IOException e) { 
			System.out.println("IOException on file closing " + e.getMessage());
		} catch (DOMException e) { 
			System.out.println("DOMException " + e.getMessage());
//		} catch (LSException e) { 
//			System.out.println("LSException " + e.getMessage());
		} catch (ClassNotFoundException e) { 
			System.out.println("ClassNotFoundException " + e.getMessage());
		} catch (InstantiationException e) { 
			System.out.println("InstantiationException " + e.getMessage());
		} catch (IllegalAccessException e) { 
			System.out.println("IllegalAccessException " + e.getMessage());
		}
		return null;
	}
}
