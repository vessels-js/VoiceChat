/**
 * Copyright (c) 2015, Gliby. * http://www.gliby.net/
 */
package net.gliby.voicechat.common.api.events;

import net.gliby.voicechat.common.networking.DataManager;
import net.gliby.voicechat.common.networking.DataStream;
import net.gliby.voicechat.common.networking.ServerDatalet;
import cpw.mods.fml.common.eventhandler.Event;

public class ServerStreamEvent extends Event {

	public static class StreamCreated extends ServerStreamEvent {
		public ServerDatalet dataLet;

		public StreamCreated(DataManager dataManager, DataStream stream, ServerDatalet dataLet) {
			super(dataManager, stream);
			this.dataLet = dataLet;
		}
	}
	public static class StreamDestroyed extends ServerStreamEvent {
		public StreamDestroyed(DataManager dataManager, DataStream stream) {
			super(dataManager, stream);
		}
	}

	public static class StreamFeed extends ServerStreamEvent {
		public ServerDatalet dataLet;

		public StreamFeed(DataManager dataManager, DataStream stream, ServerDatalet dataLet) {
			super(dataManager, stream);
			this.dataLet = dataLet;
		}
	}

	public DataStream stream;

	public DataManager dataManager;

	public ServerStreamEvent(DataManager dataManager, DataStream stream) {
		this.dataManager = dataManager;
		this.stream = stream;
	}

}
