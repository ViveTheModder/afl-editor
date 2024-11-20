# afl-editor
A command-line tool that finds & replaces strings in AFL files (AFS File Lists).
# Demonstration
## Example 1
A single AFL taken from DBZ Budokai Tenkaichi 1. Here, I replace ``bgm`` with ``hello there``, which contains a space.

![afl-editor-1](https://github.com/user-attachments/assets/42ceeba6-f378-4bc3-8d3a-7b3cfabd35b8)
## Example 2
Several AFLs from DBZ Budokai Tenkaichi 3, both the final version and its prototype. 

Here, I replace ``Akuman`` with ``Vive_The_Modder``, and since there are underscores, quotes and spaces are unnecessary.

Also, ``PZS3US1_PROTO_OG.AFL`` is skipped because its number of files does not properly line up with the AFL's file size.
![afl-editor-2](https://github.com/user-attachments/assets/26a3a882-e1ab-4275-bb7b-343457f944f5)
# Notes
1. The ``-ra`` and ``-rf`` refer to instances of the string to find within **each file name**, not the whole AFL.

So, using ``-rf`` will not make one replacement for the whole AFL, but rather only one replacement for each file name.
2. In case it was not obvious, this tool was made purely because [HiroTex](https://github.com/HiroTex)'s take on an AFL editor is:
* written in C# (aka Java: Microsoft Edition), oh mama;
* only meant to be used on Windows, as running it through WINE on Linux apparently causes issues;
* able to edit one AFL at a time, whereas mine allows for several to be edited, as long as they are all in one folder.

The only upside is that his tool has some GUI. That's it. Mine will have it too, but CLI is not going anywhere...
