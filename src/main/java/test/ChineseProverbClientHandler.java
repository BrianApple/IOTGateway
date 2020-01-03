package test;


import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 * 
 * @Description: 
 * @author  yangcheng
 * @date:   2019年8月15日
 */
public class ChineseProverbClientHandler extends
	SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg)
	    throws Exception {
		String response = ByteBufUtil.hexDump(msg.content());
		
	    System.out.println("收到响应数据"+response+";index = " + CountHelper.clientRecieveCount.addAndGet(1));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	    throws Exception {
		cause.printStackTrace();
		ctx.close();
    }
}
