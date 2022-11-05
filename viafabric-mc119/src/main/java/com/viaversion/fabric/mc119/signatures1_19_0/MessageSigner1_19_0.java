package com.viaversion.fabric.mc119.signatures1_19_0;

import net.minecraft.network.encryption.Signer;
import net.minecraft.network.message.MessageMetadata;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

public class MessageSigner1_19_0 {
   public static final Deque<MessageMetadata> TRACKED_METADATA_LIST = new ArrayDeque<>();

   public static MessageMetadata pollLastMetadata() {
      return TRACKED_METADATA_LIST.pollLast();
   }

   public static void track(final MessageMetadata metadata) {
      TRACKED_METADATA_LIST.add(metadata);
   }

   public static MessageSignatureData sign(Signer signer, Text decorateText, final UUID sender, final Instant timeStamp, final long salt) {
      return new MessageSignatureData(signer.sign((sign -> {
         final byte[] data = new byte[32];

         final ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);

         buffer.putLong(salt);
         buffer.putLong(sender.getMostSignificantBits()).putLong(sender.getLeastSignificantBits());
         buffer.putLong(timeStamp.getEpochSecond());

         sign.update(data);
         sign.update(Text.Serializer.toSortedJsonString(decorateText).getBytes(StandardCharsets.UTF_8));
      })));
   }
}
