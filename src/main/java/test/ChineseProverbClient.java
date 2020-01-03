package test;



import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

/**
 * 
 * @Description: 
 * @author  yangcheng
 * @date:   2019年8月15日
 */
public class ChineseProverbClient {

    public void run(int port) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
		    Bootstrap b = new Bootstrap();
		    b.group(group).channel(NioDatagramChannel.class)
			    .option(ChannelOption.SO_BROADCAST, true)
			    .handler(new ChineseProverbClientHandler());
		    Channel ch = b.bind(0).sync().channel();
		    for(int i = 0 ; i< 10000 ; i++){
		    	ch.writeAndFlush(
		    		    new DatagramPacket(Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump("40402300010102090E0C0C129FB00100000000000000000004000219010101DA2323")), new InetSocketAddress(
		    			    "127.0.0.1", port))).sync().await();
		    }
		    Thread.sleep(Integer.MAX_VALUE);
		    if (!ch.closeFuture().await(15000)) {
		    	System.out.println("查询超时!");
		    }
		    
		} finally {
		    group.shutdownGracefully();
		}
    }

    public static void main(String[] args) throws Exception {
		int port = 9811;
		if (args.length > 0) {
		    try {
			port = Integer.parseInt(args[0]);
		    } catch (NumberFormatException e) {
			e.printStackTrace();
		    }
		}
		new ChineseProverbClient().run(port);
    }
}
