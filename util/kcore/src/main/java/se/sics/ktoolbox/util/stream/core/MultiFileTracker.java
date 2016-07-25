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
package se.sics.ktoolbox.util.stream.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.javatuples.Pair;
import se.sics.kompics.ComponentProxy;
import se.sics.kompics.config.Config;
import se.sics.ktoolbox.util.result.DelayedExceptionSyncHandler;
import se.sics.ktoolbox.util.stream.StreamResource;
import se.sics.ktoolbox.util.stream.TransferMngr;
import se.sics.ktoolbox.util.stream.buffer.KBuffer;
import se.sics.ktoolbox.util.stream.buffer.MultiKBuffer;
import se.sics.ktoolbox.util.stream.buffer.SimpleAppendKBuffer;
import se.sics.ktoolbox.util.stream.cache.SimpleKCache;
import se.sics.ktoolbox.util.stream.storage.AsyncAppendStorage;
import se.sics.ktoolbox.util.stream.storage.AsyncCompleteStorage;
import se.sics.ktoolbox.util.stream.storage.AsyncOnDemandHashStorage;
import se.sics.ktoolbox.util.stream.storage.managed.AppendFileMngr;
import se.sics.ktoolbox.util.stream.storage.managed.CompleteFileMngr;
import se.sics.ktoolbox.util.stream.transfer.DownloadTransferMngr;
import se.sics.ktoolbox.util.stream.transfer.UploadTransferMngr;
import se.sics.ktoolbox.util.stream.util.FileDetails;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class MultiFileTracker {

    private final Map<Integer, UploadTransferMngr> completed = new HashMap<>();
    private final Map<Integer, DownloadTransferMngr> pendingComplete = new HashMap<>();
    private final TreeMap<Integer, DownloadTransferMngr> ongoing = new TreeMap<>();

    public MultiFileTracker(Config config, ComponentProxy proxy, DelayedExceptionSyncHandler exSyncHandler,
            Map<Integer, FileDetails> transferDetails, boolean complete) {
        for (Map.Entry<Integer, FileDetails> entry : transferDetails.entrySet()) {
            FileDetails fileDetails = entry.getValue();
            if (complete) {
                AsyncCompleteStorage file = new AsyncCompleteStorage(config, proxy, exSyncHandler, fileDetails);
                AsyncOnDemandHashStorage hash = new AsyncOnDemandHashStorage(fileDetails, exSyncHandler, file);
                CompleteFileMngr fileMngr = new CompleteFileMngr(fileDetails, file, hash);
                completed.put(entry.getKey(), new UploadTransferMngr(fileDetails, fileMngr));
            } else {
                SimpleKCache cache = new SimpleKCache(config, proxy, exSyncHandler, fileDetails.mainResource);
                List<KBuffer> bufs = new ArrayList<>();
                bufs.add(new SimpleAppendKBuffer(config, proxy, exSyncHandler, fileDetails.mainResource, 0));
                for (StreamResource writeResource : fileDetails.secondaryResources) {
                    bufs.add(new SimpleAppendKBuffer(config, proxy, exSyncHandler, writeResource, 0));
                }
                KBuffer buffer = new MultiKBuffer(bufs);
                AsyncAppendStorage file = new AsyncAppendStorage(cache, buffer);
                AsyncOnDemandHashStorage hash = new AsyncOnDemandHashStorage(entry.getValue(), exSyncHandler, file);
                AppendFileMngr fileMngr = new AppendFileMngr(entry.getValue(), file, hash);
                ongoing.put(entry.getKey(), new DownloadTransferMngr(entry.getValue(), fileMngr));
            }
        }
    }

    public void complete(int file) {
        DownloadTransferMngr ongoingFileMngr = pendingComplete.remove(file);
        if (ongoingFileMngr == null) {
            ongoingFileMngr = ongoing.remove(file);
        }
        UploadTransferMngr completedFileMngr = ongoingFileMngr.complete();
        completed.put(file, completedFileMngr);
    }

    public TransferMngr.Reader readFrom(int file) {
        TransferMngr.Reader transferMngr = completed.get(file);
        if (transferMngr == null) {
            transferMngr = pendingComplete.get(file);
        }
        if (transferMngr == null) {
            transferMngr = ongoing.get(file);
        }
        return transferMngr;
    }

    public TransferMngr.Writer writeTo(int file) {
        TransferMngr.Writer transferMngr = ongoing.get(file);
        if (transferMngr == null) {
            transferMngr = pendingComplete.get(file);
        }
        return transferMngr;
    }

    public void pendingComplete(int file) {
        pendingComplete.put(file, ongoing.remove(file));
    }

    public Pair<Integer, TransferMngr.Writer> nextOngoing() {
        Pair<Integer, TransferMngr.Writer> next = Pair.with(ongoing.firstEntry().getKey(), (TransferMngr.Writer) ongoing.firstEntry().getValue());
        return next;
    }
}