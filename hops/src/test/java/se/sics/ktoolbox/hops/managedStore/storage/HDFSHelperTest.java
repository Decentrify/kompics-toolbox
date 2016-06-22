/*
 * Copyright (C) 2009 Swedish Institute of Computer Science (SICS) Copyright (C)
 * 2009 Royal Institute of Technology (KTH)
 *
 * KompicsToolbox is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.sics.ktoolbox.hops.managedStore.storage;

import java.util.Random;
import org.junit.Test;
import se.sics.ktoolbox.hops.managedStore.storage.util.HDFSResource;

/**
 *
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class HDFSHelperTest {

//    @Test
//    public void simpleAppend() throws InterruptedException {
//        HDFSResource resource = new HDFSResource("bbc1.sics.se", 26801, "/experiment/download/", "test4");
//        String user = "glassfish";
//        Random rand = new Random(123);
//        byte[] data;
//
//        HDFSHelper.delete(resource, user);
//        HDFSHelper.simpleCreate(resource, user);
//        System.err.println("file created");
//        data = new byte[1024];
//        rand.nextBytes(data);
//        System.err.println("appending 1");
//        HDFSHelper.append(resource, user, data);
//        System.err.println("appended 1");
//
//        for (int i = 0; i < 100; i++) {
//            data = new byte[1024 * 1024];
//            rand.nextBytes(data);
//            System.err.println("appending" + i);
//            HDFSHelper.append(resource, user, data);
//            System.err.println("appended" + i);
//        }
//    }

//    @Test
//    public void simpleCreate() {
//        HDFSResource resource = new HDFSResource("bbc1.sics.se", 26801, "/experiment/download/", "file");
//        String user = "glassfish";
//        HDFSHelper.delete(resource, user);
//        HDFSHelper.create(resource, user, 10*1000*1000);
//    }
}
