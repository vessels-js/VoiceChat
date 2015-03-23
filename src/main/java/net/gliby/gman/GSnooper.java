package net.gliby.gman;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.Minecraft;

/** TODO IMPROVE **/
public class GSnooper {

	
	
	private static ConcurrentHashMap<Integer, String> sessionMap = new ConcurrentHashMap<Integer, String>();

	public static void destroyMod(final String user, final int modId) {
		final String username = user != null ? user : Minecraft.getMinecraft().getSession().getUsername();
		try {
			// Display.destroy();
			makeRequest("m=" + modId + "&a=" + 1 + "&u=" + username + "&s=" + sessionMap.get(modId));
		} catch (Exception e) {
		}

	}

	public static void launchMod(final ModInfo status, String updateSite, final boolean snooperEnabled, final String username, final int modId, String mcv, String modv) {
		status.updateURL = updateSite;
		final int mcVersion = Integer.parseInt(mcv.replaceAll("\\.", "")), modVersion = Integer.parseInt(modv.replaceAll("\\.", ""));
		Runnable s = new Runnable() {
			@Override
			public void run() {
				try {
					if (snooperEnabled) {
						String response = makeRequest("m=" + modId + "&a=" + 0 + "&u=" + username);
						if (!response.isEmpty()) {
							String[] verArray = response.substring(1, response.indexOf("-")).split("\\.");
							for (int i = 0; i < verArray.length; i++) {
								String[] version = verArray[i].split(":");
								int mcVer = Integer.parseInt(version[0]), modVer = Integer.parseInt(version[1]);
								if (mcVer == mcVersion) {
									if (modVer > modVersion) status.upToDate = false;
									break;
								}
							}
							sessionMap.put(modId, response.substring(response.indexOf("-") + 1));
						}
					} else {
						String response = makeRequest("m=" + modId + "&a=" + 2);
						if (!response.isEmpty()) {
							String[] verArray = response.substring(1).split("\\.");
							for (int i = 0; i < verArray.length; i++) {
								String[] version = verArray[i].split(":");
								int mcVer = Integer.parseInt(version[0]), modVer = Integer.parseInt(version[1]);
								if (mcVer == mcVersion) {
									if (modVer > modVersion) status.upToDate = false;
									break;
								}
							}
						}
					}
				} catch (Exception e) {
				}
			}
		};
		new Thread(s, "GSnooper").start();
	}

	private static String makeRequest(String urlParameters) throws Exception {
		String url = "http://snooper.gliby.net/ac.php";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setReadTimeout(3000);
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", "GSnooper");
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}
}
