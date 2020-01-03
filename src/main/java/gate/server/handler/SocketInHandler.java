package gate.server.handler;

import gate.base.cache.ClientChannelCache;
import gate.base.chachequeue.CacheQueue;
import gate.base.config.Config;
import gate.base.domain.ChannelData;
import gate.base.domain.SocketData;
import gate.kafka.Tmnl2KK;
import gate.util.CommonUtil;
import gate.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCountUtil;
import io.netty.channel.ChannelHandler.Sharable;
/**
 * 
 * @Description: 
 * @author  yangcheng
 * @date:   2019年3月30日
 */
@Sharable
public class SocketInHandler extends SimpleChannelInboundHandler<DatagramPacket>{


	@Override
    public void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
		StringBuilder sb = null;
		ByteBuf bufData = packet.content();
		
		packet.sender().getHostString();
		String ipAddress = StringUtils.formatIpAddress(packet.sender().getHostString(), String.valueOf(packet.sender().getPort()));
		ClientChannelCache.set(ipAddress, ctx.channel());
		if(!Config.ENABLE_MQTT){
			sb = new StringBuilder();
	    	sb.append(ipAddress).append(CommonUtil.ipDataSplit).append(ByteBufUtil.hexDump(bufData));
	    	ReferenceCountUtil.release(bufData);
			Tmnl2KK.sendMsgAsynch(sb.toString());
		}else{
			SocketData data = new SocketData(bufData.retain());
			ChannelData channelData =  new ChannelData(ipAddress, data);
			CacheQueue.up2MasterQueue.put(channelData);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		
		ctx.close();
		cause.printStackTrace();
	}

}
