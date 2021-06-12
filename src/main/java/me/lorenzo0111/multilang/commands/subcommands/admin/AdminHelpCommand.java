/*
 * This file is part of MultiLang, licensed under the MIT License.
 *
 * Copyright (c) Lorenzo0111
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

package me.lorenzo0111.multilang.commands.subcommands.admin;

import me.lorenzo0111.multilang.commands.SubCommand;
import me.lorenzo0111.multilang.commands.subcommands.HelpCommand;
import me.lorenzo0111.pluginslib.command.Command;

import java.util.List;

public class AdminHelpCommand extends HelpCommand {

    public AdminHelpCommand(Command command, List<SubCommand> subCommands) {
        super(command, subCommands);
    }

    @Override
    public String formatHelp(String command, String description) {
        return this.format("&9/alang %s &8» &7%s", command, description);
    }
}
