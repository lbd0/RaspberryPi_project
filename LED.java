package project;
import java.util.List;

import org.ws4d.coap.core.enumerations.CoapMediaType;
import org.ws4d.coap.core.rest.BasicCoapResource;
import org.ws4d.coap.core.rest.CoapData;
import org.ws4d.coap.core.tools.Encoder;
import com.pi4j.io.gpio.*;

public class LED extends BasicCoapResource{
	private String state = "off";
	private int user_blight = 0;
	private int light_sensor = 0;
	
	GpioController gpio;
	GpioPinPwmOutput r_led;
	GpioPinPwmOutput g_led;
	GpioPinPwmOutput b_led;
	
	

	private LED(String path, String value, CoapMediaType mediaType) {
		super(path, value, mediaType);
	}

	public LED() {
		this("/led", "off", CoapMediaType.text_plain);

		// 소스코드 추가 작성		
		gpio = GpioFactory.getInstance();
		r_led  = gpio.provisionPwmOutputPin(RaspiPin.GPIO_24);
		g_led = gpio.provisionPwmOutputPin(RaspiPin.GPIO_26);
		b_led = gpio.provisionPwmOutputPin(RaspiPin.GPIO_01);
		
		r_led.setPwm(0);
		g_led.setPwm(0);
		b_led.setPwm(0);
	
	}

	@Override
	public synchronized CoapData get(List<String> query, List<CoapMediaType> mediaTypesAccepted) {
		return get(mediaTypesAccepted);
	}
	
	@Override
	public synchronized CoapData get(List<CoapMediaType> mediaTypesAccepted) {
		return new CoapData(Encoder.StringToByte(this.state), CoapMediaType.text_plain);
	}

	@Override
	public synchronized boolean setValue(byte[] value) {
		this.state = Encoder.ByteToString(value);
		
		if(this.state.equals("on")) {
			try {
				int blight = 0;
				switch(user_blight) {
					case 20 : 
						blight = user_blight; 
						break;
					case 115 : 
						blight = user_blight; 
						break;
					case 200 : 
						blight = user_blight; 
						break;
					case 230 : 
						System.out.println(user_blight);
						blight = user_blight; 
						break;
					default : 
						MCP3204 obj = new MCP3204();
						try {
							light_sensor = obj.readMCP3204(0);
							blight = map(light_sensor, 400, 2000, 0, 100);
						}catch(Exception e) {
							System.out.println(e);
						}
					break;
				}
				System.out.println("조도센서 값 : " + light_sensor + ", led 밝기 : " + blight);
				r_led.setPwm(blight);
				g_led.setPwm(blight);
				b_led.setPwm(blight);
				
				Thread.sleep(500);
			}catch(Exception e) {
				System.out.println(e);
			}
			
		}else if(this.state.equals("off")){
			r_led.setPwm(0);
			g_led.setPwm(0);
			b_led.setPwm(0);
		}else if(this.state.equals("20")) {
			user_blight = 20;
		}else if(this.state.equals("50")) {
			user_blight = 115;
		}else if(this.state.equals("80")) {
			user_blight = 200;
		}else if(this.state.equals("100")) {
			user_blight = 230;
			
		}else if(this.state.equals("reset")) {
			user_blight = 0;
		}else {
			r_led.setPwm(0);
			g_led.setPwm(0);
			b_led.setPwm(0);
		}
		
		return true;
	}
	
	private int map(int x, int in_min, int in_max, int out_min, int out_max) {
		return (x - in_min)*(out_max - out_min)/(in_max - in_min)+out_min;
	}
	
	@Override
	public synchronized boolean post(byte[] data, CoapMediaType type) {
		return this.setValue(data);
	}

	@Override
	public synchronized boolean put(byte[] data, CoapMediaType type) {
		return this.setValue(data);
	}

	@Override
	public synchronized String getResourceType() {
		return "Raspberry pi 4 LED";
	}

}