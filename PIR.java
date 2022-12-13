package project;

import java.util.List;

import org.ws4d.coap.core.enumerations.CoapMediaType;
import org.ws4d.coap.core.rest.BasicCoapResource;
import org.ws4d.coap.core.rest.CoapData;
import org.ws4d.coap.core.tools.Encoder;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class PIR extends BasicCoapResource {
	private String value = "false";
	
	GpioController gpio;
	GpioPinDigitalInput pir;
	boolean pir_state = false;
	boolean pir_on = true;
	
	private PIR(String path, String value, CoapMediaType mediaType) {
		super(path, value, mediaType);
	}
	
	public PIR() {
		this("/pir", "false", CoapMediaType.text_plain);
		
		gpio = GpioFactory.getInstance();
		pir = gpio.provisionDigitalInputPin(RaspiPin.GPIO_25);
	}
	
	public synchronized CoapData get(List<String> query, List<CoapMediaType> mediaTypesAccepted) {
		return get(mediaTypesAccepted);
	}
	
	public synchronized void optional_changed() {
		// 움직임 감지값 읽기
		if(pir_on) {
			pir_state = pir.isHigh();
			String state = Boolean.toString(pir_state);
			
			if(state.equals(this.value)) {
				System.out.println("움직임이 감지되지 않았습니다.");
				
			} else {
				System.out.println("움직임이 감지되었습니다.");
				this.changed(state);
				this.value = state;
			}
		}else {
			this.changed("false");
			System.out.println("움직임을 감지하지 않습니다.");
		}
	}
	@Override
	public synchronized CoapData get(List<CoapMediaType> mediaTypesAccepted) {
		boolean sensing_data = pir.isHigh();
		this.value = Boolean.toString(sensing_data);
		return new CoapData(Encoder.StringToByte(this.value), CoapMediaType.text_plain);
	}
	
	@Override
	public synchronized boolean setValue(byte[] value) {
		this.value = Encoder.ByteToString(value);
		
		if(this.value.equals("off")) {
			pir_on = false;
			
		}else if(this.value.equals("on")) {
			pir_on = true;
		}
		return true;
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
		return "Raspberry pi 4 Temperature Sensor";
	}

}
