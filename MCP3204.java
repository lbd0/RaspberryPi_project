package project;

import java.io.IOException;
import com.pi4j.io.spi.*;

public class MCP3204 {
	public static SpiDevice spi = null;
	public MCP3204() {
		try {
			//SPI 객체 선언 
			spi = SpiFactory.getInstance(SpiChannel.CS0, SpiDevice.DEFAULT_SPI_SPEED, SpiDevice.DEFAULT_SPI_MODE);
		}catch (Exception e) {
			System.out.println("Fail to create a SPI instance");
		}		
	}
	
	public static String byteToBinaryString(byte n) {
		// Byte의 binary 값을 String으로 반환
	    StringBuilder sb = new StringBuilder("00000000");
	    for (int bit = 0; bit < 8; bit++) {
	        if (((n >> bit) & 1) > 0) {
	            sb.setCharAt(7 - bit, '1');
	        }
	    }
	    return sb.toString();
	}
	
	public int readMCP3204(int adcChannel) throws IOException {
		byte[] sending_data = {0,0,0};
		byte[] receiving_data;
		
		sending_data[0] = (byte)(sending_data[0] | 0b11100000);
		
		if(adcChannel >= 2) {
			sending_data[0] = (byte)(sending_data[0] | 0b00010000);
		}
		
		if(adcChannel % 2 == 1) {
			sending_data[0] = (byte)(sending_data[0] | 0b00001000);
		}
		
		receiving_data = spi.write(sending_data);
		
		String binaryString = byteToBinaryString(receiving_data[0]) 
				+ byteToBinaryString(receiving_data[1])
				+ byteToBinaryString(receiving_data[2]);
		
		int value = Integer.parseInt(binaryString.substring(7, 19), 2);
		
		return value;
		// 실습자료 PPT를 참고하여 소스코드 작성 후, 실행할 것 
	}

	private static int map(int x, int in_min, int in_max, int out_min, int out_max) {
		return (x - in_min)*(out_max - out_min)/(in_max - in_min)+out_min;
	}
	
	public static void main(String[] args) {
		MCP3204 obj = new MCP3204();
		while(true) {
			try {
				int value = obj.readMCP3204(0); // CH0 
				int blight = map(value, 0, 1024, 0, 100);
				System.out.println(value + ", " + blight); // 조도센서 값 출력
				Thread.sleep(1000);
			}catch (Exception e) {
				System.out.println(e);
			}
		}
	}
}
