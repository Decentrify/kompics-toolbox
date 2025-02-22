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
package se.sics.ktoolbox.nutil.conn.workers;

import se.sics.kompics.KompicsEvent;
import se.sics.kompics.util.Identifiable;
import se.sics.kompics.util.Identifier;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class WorkCtrlCenterEvents {

  public static abstract class Base implements KompicsEvent, Identifiable {

    public final Identifier eventId;

    public Base(Identifier eventId) {
      this.eventId = eventId;
    }

    @Override
    public Identifier getId() {
      return eventId;
    }
  }

  public static class NewTask extends Base {

    public final WorkTask.Request task;

    public NewTask(Identifier eventId, WorkTask.Request task) {
      super(eventId);
      this.task = task;
    }

    public TaskStatus update(WorkTask.Status status) {
      return new TaskStatus(task, eventId, status);
    }

    public TaskCompleted completed(WorkTask.Result result) {
      return new TaskCompleted(eventId, task, result);
    }

    @Override
    public String toString() {
      return "NewTask{" + "task=" + task + '}';
    }
  }

  public static class TaskCompleted extends Base {

    public final WorkTask.Request task;
    public final WorkTask.Result result;

    public TaskCompleted(Identifier eventId, WorkTask.Request task, WorkTask.Result result) {
      super(eventId);
      this.task = task;
      this.result = result;
    }
  }

  public static class TaskStatus extends Base {

    public final WorkTask.Request task;
    public final WorkTask.Status status;

    public TaskStatus(WorkTask.Request task, Identifier eventId, WorkTask.Status status) {
      super(eventId);
      this.task = task;
      this.status = status;
    }
  }
}
