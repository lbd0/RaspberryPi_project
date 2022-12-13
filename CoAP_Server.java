package project;

import org.ws4d.coap.core.connection.api.CoapClientChannel;
import org.ws4d.coap.core.enumerations.CoapMediaType;
import org.ws4d.coap.core.enumerations.CoapRequestCode;
import org.ws4d.coap.core.messages.api.CoapRequest;
import org.ws4d.coap.core.rest.CoapData;
import org.ws4d.coap.core.rest.CoapResourceServer;

public class CoAP_Server {
	private static CoAP_Server coapServer;
	private CoapResourceServer resourceServer;
	CoapClientChannel clientChannel = null;
	
	public static void main(String[] args) {
		coapServer = new CoAP_Server();
		coapServer.start();
	}

	public void start() {
		System.out.println("===Run CoAP Server ===");

		// create server
		if (this.resourceServer != null)	this.resourceServer.stop();
		this.resourceServer = new CoapResourceServer();

		// initialize resource
		LED led = new LED();
		PIR pir = new PIR();
		
		// CoapResourceServer에 observe하려는 resource 등록
		pir.registerServerListener(resourceServer);
		
		// add resource to server
		this.resourceServer.createResource(pir);
		this.resourceServer.createResource(led);
				
		// run the server
		try {
			this.resourceServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		while(true) {
			try {
				Thread.sleep(1000); 	// observe 주기
				pir.optional_changed();	
			}catch(Exception e) {
				e.printStackTrace();
			}
		}

	}
}

