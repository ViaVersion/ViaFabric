/*
 * MIT License
 *
 * Copyright (c) 2018 creeper123123321 and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.creeper123123321.viafabric;

import com.github.creeper123123321.viafabric.util.JLoggerToLog4j;
import com.github.creeper123123321.viafabric.util.Version;
import com.google.common.io.CharStreams;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.SharedConstants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViaFabric implements ModInitializer {
    public static final java.util.logging.Logger JLOGGER = new JLoggerToLog4j(LogManager.getLogger("ViaFabric"));
    public static final ExecutorService ASYNC_EXECUTOR;
    public static final EventLoop EVENT_LOOP;

    static {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("ViaFabric").build();
        ASYNC_EXECUTOR = Executors.newCachedThreadPool(factory);
        EVENT_LOOP = new DefaultEventLoop(factory);
    }

    public static String getVersion() {
        return FabricLoader.INSTANCE.getModContainers()
                .stream()
                .filter(container -> container.getInfo().getId().equals("viafabric"))
                .findFirst()
                .get().getInfo().getVersionString();
    }

    private void checkForUpdates(File jar, String artifactName, String groupIdPath, String depName) throws Exception {
        int timeDivisor = 1000 * 60 * 60 * 24;
        long cachedTime = System.currentTimeMillis() / timeDivisor;
        if (!(jar.exists() && jar.lastModified() / timeDivisor == cachedTime)) {
            String localMd5 = null;
            if (jar.exists()) {
                try (InputStream is = Files.newInputStream(jar.toPath())) {
                    localMd5 = DigestUtils.md5Hex(is);
                }
            }
            URL versionsUrl = new URL("https://repo.viaversion.com/" + groupIdPath + "/" + artifactName + "/?" + cachedTime);
            JLOGGER.info("Checking for " + depName + " updates " + versionsUrl);
            HttpURLConnection con = (HttpURLConnection) versionsUrl.openConnection();
            con.setRequestProperty("User-Agent", "ViaFabric/" + ViaFabric.getVersion());
            String rawOutput = CharStreams.toString(new InputStreamReader(con.getInputStream()));
            Pattern urlPattern = Pattern.compile("<A href='([^']*)/'>");
            Matcher matcher = urlPattern.matcher(rawOutput);
            List<String> versions = new ArrayList<>();
            while (matcher.find()) {
                versions.add(matcher.group(1));
            }
            String mcVersion = SharedConstants.getGameVersion().getName();
            String bestViaVersion = null;
            if (mcVersion.contains("w") || mcVersion.contains("-")) {
                bestViaVersion = versions.stream()
                        .filter(it -> it.endsWith(mcVersion))
                        .max(Comparator.comparing(Version::new))
                        .orElse(null);
            }
            if (bestViaVersion == null) {
                bestViaVersion = versions.stream()
                        .filter(it -> it.endsWith("-SNAPSHOT") || it.endsWith("-DEV") || !it.contains("-"))
                        .max(Comparator.comparing(Version::new))
                        .orElse(null);
            }
            HttpURLConnection md5Con = (HttpURLConnection) new URL(
                    "https://repo.viaversion.com/" + groupIdPath + "/" + artifactName + "/" + bestViaVersion
                            + "/" + artifactName + "-" + bestViaVersion + ".jar.md5").openConnection();
            md5Con.setRequestProperty("User-Agent", "ViaFabric/" + ViaFabric.getVersion());
            String remoteMd5 = CharStreams.toString(new InputStreamReader(md5Con.getInputStream()));
            if (!remoteMd5.equals(localMd5)) {
                URL url = new URL("https://repo.viaversion.com/" + groupIdPath + "/" + artifactName + "/" + bestViaVersion
                        + "/" + artifactName + "-" + bestViaVersion + ".jar");
                ViaFabric.JLOGGER.info("Downloading " + url);
                HttpURLConnection jarCon = (HttpURLConnection) url.openConnection();
                jarCon.setRequestProperty("User-Agent", "ViaFabric/" + ViaFabric.getVersion());
                FileUtils.copyInputStreamToFile(jarCon.getInputStream(), jar);
            } else {
                JLOGGER.info("No updates found");
                jar.setLastModified(System.currentTimeMillis());
            }
        }
    }

    @Override
    public void onInitialize() {
        File viaVersionJar = FabricLoader.INSTANCE.getConfigDirectory().toPath().resolve("ViaFabric").resolve("viaversion.jar").toFile();
        try {
            checkForUpdates(viaVersionJar, "viaversion", "us/myles", "ViaVersion");
        } catch (Exception e) {
            e.printStackTrace();
        }
        File viaRewindJar = FabricLoader.INSTANCE.getConfigDirectory().toPath().resolve("ViaFabric").resolve("viarewind.jar").toFile();
        try {
            checkForUpdates(viaRewindJar, "viarewind-all", "de/gerrygames", "ViaRewind");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Method addUrl = ViaFabric.class.getClassLoader().getClass().getMethod("addURL", URL.class);
            addUrl.setAccessible(true);
            addUrl.invoke(ViaFabric.class.getClassLoader(), viaVersionJar.toURI().toURL());
            addUrl.invoke(ViaFabric.class.getClassLoader(), viaRewindJar.toURI().toURL());
            Class.forName("com.github.creeper123123321.viafabric.VRViaVersionInitializer")
                    .getMethod("init")
                    .invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | MalformedURLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
