/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2024 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.viaversion.fabric.common;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.fastutil.ints.IntArrayList;
import com.viaversion.viaversion.libs.fastutil.ints.IntList;
import org.jetbrains.annotations.CheckReturnValue;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests AddressParser's capability to parse addresses into a useful form,
 * extracting Via metadata and providing addresses Minecraft and Guava can parse.
 * <p>
 * Test cases include seemingly malformed data that previous versions of ViaFabric
 * would accept as valid, and asserting that it would parse and treat it the same way.
 **/
public class AddressParserTest {

    private static IntList of(ProtocolVersion... versions) {
        return Arrays.stream(versions).mapToInt(ProtocolVersion::getVersion)
                .collect(IntArrayList::new, IntList::add, IntList::addAll);
    }

    /**
     * Ensures that:
     * <ul>
     *     <li>{@link AddressParser#hasViaMetadata()} returns true, as via metadata is provided.</li>
     *     <li>{@link AddressParser#hasProtocol()} returns false when {@code versions} is empty, true otherwise.</li>
     *     <li>{@link AddressParser#protocols} returns all parsed protocols and matches {@code versions}</li>
     *     <li>{@link AddressParser#protocol()} matches the first protocol listed. Subject to change.</li>
     * </ul>
     */
    @Test(dataProvider = "viaAddrProvider", timeOut = 1000L)
    public void viaAddrHarness(String rawAddress, String address, Integer port, ProtocolVersion... versions) {
        final AddressParser viaAddr = AddressParser.parse(rawAddress);
        assertFundamentals(viaAddr, address, port);

        assertTrue(viaAddr.hasViaMetadata(), "hasViaMetadata");

        // Tests protocol parsing to ensure it is returning expected values.
        assertEquals(viaAddr.protocols, of(versions));
        if (versions.length == 0) {
            assertFalse(viaAddr.hasProtocol(), "hasProtocol");
            assertNull(viaAddr.protocol());
        } else {
            assertTrue(viaAddr.hasProtocol(), "hasProtocol");
            assertNotNull(viaAddr.protocol(), "protocol");
            assertEquals((int) viaAddr.protocol(), versions[0].getVersion());
        }
    }

    /**
     * Ensures that:
     * <ul>
     *     <li>{@link AddressParser#hasViaMetadata()} returns false, as via metadata is <em>not</em> provided.</li>
     *     <li>{@link AddressParser#hasProtocol()} returns false.</li>
     *     <li>{@link AddressParser#protocols} is null. Subject to change.</li>
     *     <li>{@link AddressParser#protocol()} is null.</li>
     * </ul>
     */
    @Test(dataProvider = "addrProvider", timeOut = 1000L)
    public void addrHarness(String rawAddress, String address, Integer port) {
        final AddressParser viaAddr = AddressParser.parse(rawAddress);
        assertFundamentals(viaAddr, address, port);

        assertFalse(viaAddr.hasViaMetadata(), "hasViaMetadata");
        assertFalse(viaAddr.hasProtocol(), "hasProtocol");
        assertNull(viaAddr.protocols, "protocols");
        assertNull(viaAddr.protocol(), "protocol");
    }

    /**
     * Ensures that:
     * <ul>
     *     <li>{@link AddressParser#serverAddress()} is the address valid for DNS, matching {@code address}</li>
     *     <li>{@link AddressParser#port()} is a valid port matching {@code port}, should there be one declared.</li>
     *     <li>{@link AddressParser#toAddress()} matches the expected address <em>without</em> Via metadata.</li>
     * </ul>
     */
    private static void assertFundamentals(AddressParser viaAddr, String address, Integer port) {
        assertEquals(viaAddr.serverAddress(), address);
        assertEquals(viaAddr.port(), port);

        // Tests toAddress to ensure it is returning the port as expected for Minecraft's parser.
        if (port == null) {
            assertEquals(viaAddr.toAddress(), address);
        } else {
            assertEquals(viaAddr.toAddress(), address + ':' + port);
        }
    }

    @CheckReturnValue
    private static Object[] params(String address, String expected, Integer port, ProtocolVersion... versions) {
        return new Object[]{address, expected, port, versions};
    }

    @DataProvider
    public static Object[][] viaAddrProvider() {
        // NOTE: Before touching values in here, consider making sure that the parser is functioning as expected.
        // If the semantic change is expected, carry on. Otherwise, fix the parser, not the test.
        return new Object[][]{
                // == Suffixes ==
                params("[::]._v1_8.viafabric", "[::]", null,
                        ProtocolVersion.v1_8),

                params("localhost._v1_7_2._v1_16_5.viafabric", "localhost", null,
                        ProtocolVersion.v1_7_2, ProtocolVersion.v1_16_4),

                params("0.0.0.0._v1_8._v1_9.v_1_21.viafabric", "0.0.0.0", null,
                        ProtocolVersion.v1_8, ProtocolVersion.v1_9, ProtocolVersion.v1_21),

                params("[::1]._v-2.viafabric", "[::1]", null,
                        ProtocolVersion.getProtocol(-2)),

                params("127.0.0.1.viafabric", "127.0.0.1", null),

                // Ports
                params("[::].v_1_8.viafabric:25565", "[::]", 25565,
                        ProtocolVersion.v1_8),

                params("0.0.0.0.v_-2.viafabric:53", "0.0.0.0", 53,
                        ProtocolVersion.getProtocol(-2)),

                params("localhost.viafabric:853", "localhost", 853),

                // IPv6
                params("[fe80::1]._v-2.viafabric:8192", "[fe80::1]", 8192,
                        ProtocolVersion.getProtocol(-2)),

                params("::.v_-2.viafabric", "::", null,
                        ProtocolVersion.getProtocol(-2)),

                params("fe80::1._v-2.viafabric", "fe80::1", null,
                        ProtocolVersion.getProtocol(-2)),


                // == Prefixes ==
                params("viafabric.v1.8;[::]", "[::]", null,
                        ProtocolVersion.v1_8),

                params("viafabric.v1.7.2+v1.16.5;localhost", "localhost", null,
                        ProtocolVersion.v1_7_2, ProtocolVersion.v1_16_4),

                params("viafabric.v1.8+v1.9+v1.21;0.0.0.0", "0.0.0.0", null,
                        ProtocolVersion.v1_8, ProtocolVersion.v1_9, ProtocolVersion.v1_21),

                params("viafabric.v-2;[::1]", "[::1]", null,
                        ProtocolVersion.getProtocol(-2)),

                // Ports
                params("viafabric.v1.8;[::]:25565", "[::]", 25565,
                        ProtocolVersion.v1_8),

                params("viafabric.v-2;0.0.0.0:53", "0.0.0.0", 53,
                        ProtocolVersion.getProtocol(-2)),

                params("viafabric.;localhost.:853", "localhost", 853),

                // IPv6
                params("viafabric.v-2;[fe80::1]:8192", "[fe80::1]", 8192,
                        ProtocolVersion.getProtocol(-2)),

                params("viafabric.v-2;::", "::", null,
                        ProtocolVersion.getProtocol(-2)),

                params("viafabric.v-2;fe80::1", "fe80::1", null,
                        ProtocolVersion.getProtocol(-2)),

                // == Malformed ==

                params("viafabric.v-2;[::]+:25565", "[::]+", 25565,
                        ProtocolVersion.getProtocol(-2)),
                params("viafabric.v-2;0.0.0.0+:25565", "0.0.0.0+", 25565,
                        ProtocolVersion.getProtocol(-2)),
                params("viafabric.v-2;localhost+:25565", "localhost+", 25565,
                        ProtocolVersion.getProtocol(-2)),
                params("viafabric.v-2;[::]+", "[::]+", null,
                        ProtocolVersion.getProtocol(-2)),
                params("viafabric.v-2;0.0.0.0+", "0.0.0.0+", null,
                        ProtocolVersion.getProtocol(-2)),
                params("viafabric.v-2;localhost+", "localhost+", null,
                        ProtocolVersion.getProtocol(-2)),
        };
    }

    @CheckReturnValue
    private static Object[] addrParams(String address, String expected, Integer port) {
        return new Object[]{address, expected, port};
    }

    @DataProvider
    public static Object[][] addrProvider() {
        return new Object[][]{
                // Port
                addrParams("[::]:25565", "[::]", 25565),
                addrParams("[fe80::1]:25565", "[fe80::1]", 25565),
                addrParams("localhost:53", "localhost", 53),
                addrParams("0.0.0.0:853", "0.0.0.0", 853),

                // No port
                addrParams("[::1]", "[::1]", null),
                addrParams("[fe80::1]", "[fe80::1]", null),
                addrParams("localhost.", "localhost", null),
                addrParams("127.0.0.1", "127.0.0.1", null),

                // IPv6
                addrParams("::", "::", null),
                addrParams("fe80::1", "fe80::1", null),

                // Malformed
                addrParams("viafabric.v-2:[::]:25565", "viafabric.v-2:[::]", 25565),
                addrParams("viafabric.v-2:0.0.0.0:25565", "viafabric.v-2:0.0.0.0", 25565),
                addrParams("viafabric.v-2:localhost:25565", "viafabric.v-2:localhost", 25565),
                addrParams("viafabric.v-2:[::]", "viafabric.v-2:[::]", null),
                addrParams("viafabric.v-2:0.0.0.0", "viafabric.v-2:0.0.0.0", null),
                addrParams("viafabric.v-2:localhost", "viafabric.v-2:localhost", null),

                addrParams("viafabric.v-2:[::]+:25565", "viafabric.v-2:[::]+", 25565),
                addrParams("viafabric.v-2:0.0.0.0+:25565", "viafabric.v-2:0.0.0.0+", 25565),
                addrParams("viafabric.v-2:localhost+:25565", "viafabric.v-2:localhost+", 25565),
                addrParams("viafabric.v-2:[::]+", "viafabric.v-2:[::]+", null),
                addrParams("viafabric.v-2:0.0.0.0+", "viafabric.v-2:0.0.0.0+", null),
                addrParams("viafabric.v-2:localhost+", "viafabric.v-2:localhost+", null),
        };
    }
}
