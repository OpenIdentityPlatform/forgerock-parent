/**
 * Copyright (C) 2011 Akiban Technologies Inc.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package com.persistit;

import com.persistit.CLI.Arg;
import com.persistit.CLI.Cmd;
import com.persistit.Management.TaskStatus;
import com.persistit.util.Util;

public class TaskCheck extends Task {

    private String _status = "not started";
    private long _taskId = -1;
    private boolean _details;
    private boolean _clearMessages;
    private boolean _clearTasks;
    private boolean _stop;
    private boolean _suspend;
    private boolean _resume;

    @Cmd("task")
    static Task createTaskCheckTask(@Arg("taskId|long:-1:-1|Task ID to to check, or -1 for all") long taskId,
            @Arg("_flag|v|Verbose") boolean verbose,
            @Arg("_flag|m|Keep previously delivered messages") boolean keepMessages,
            @Arg("_flag|k|Keep task even if completed") boolean keepTasks, @Arg("_flag|x|Stop the task") boolean stop,
            @Arg("_flag|u|Suspend the task") boolean suspend, @Arg("_flag|r|Resume the task") boolean resume)
            throws Exception {

        TaskCheck task = new TaskCheck();
        task._taskId = taskId;
        task._details = verbose;
        task._clearTasks = !keepTasks;
        task._clearMessages = !keepMessages;
        task._stop = stop;
        task._suspend = suspend;
        task._resume = resume;
        return task;
    }

    @Override
    public boolean isImmediate() {
        return true;
    }

    void setArgs(final long taskId, final boolean verbose, final boolean removeTasks, final boolean removeMessages,
            final boolean stop, final boolean suspend, final boolean resume) {
        _taskId = taskId;
        _details = verbose;
        _clearTasks = removeTasks;
        _clearMessages = removeMessages;
        _stop = stop;
        _suspend = suspend;
        _resume = resume;
    }

    @Override
    protected void runTask() throws Exception {
        if (_stop) {
            _persistit.getManagement().stopTask(_taskId, false);
        } else if (_suspend) {
            _persistit.getManagement().setTaskSuspended(_taskId, true);
        } else if (_resume) {
            _persistit.getManagement().setTaskSuspended(_taskId, false);
        }
        TaskStatus[] status = _persistit.getManagement().queryTaskStatus(_taskId, _details, _clearMessages, _clearTasks);
        final StringBuilder sb = new StringBuilder();
        for (final TaskStatus ts : status) {
            final String s = ts.toString(_details);
            if (!s.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(Util.NEW_LINE);
                }
                sb.append(s);
            }
        }
        _status = sb.toString();

    }

    @Override
    public String getStatus() {
        return _status;
    }

}
