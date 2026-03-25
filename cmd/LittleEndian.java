package cmd;
//Little Endian class by ViveTheJoestar
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LittleEndian {
	public static int getInt(int data) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.asIntBuffer().put(data);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}
}