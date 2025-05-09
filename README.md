# afl-editor
A **Java** tool (supporting both CLI and GUI) that **finds & replaces strings in AFL files** (AFS File Lists).

It can also prevent duplicate file names by adding the file ID at the end of said duplicates.

That way, AFS Explorer can export these files independently, without them overwriting each other.
# Demonstration
## Example 1
A **single AFL** taken from DBZ Budokai Tenkaichi 1. Here, I replace ``bgm`` with ``hello there``, which **contains a space**.

![afl-editor-1](https://github.com/user-attachments/assets/42ceeba6-f378-4bc3-8d3a-7b3cfabd35b8)
## Example 2
**Several AFLs** from DBZ Budokai Tenkaichi 3, both the final version and its prototype. 

Here, I replace ``Akuman`` with ``Vive_The_Modder``, and since **there are underscores**, **quotes and spaces are unnecessary**.

Also, ``PZS3US1_PROTO_OG.AFL`` is **skipped** because **its number of files does not properly line up** with the AFL's file size.
![afl-editor-2](https://github.com/user-attachments/assets/26a3a882-e1ab-4275-bb7b-343457f944f5)
## Example 3
Screenshots of the tool working on Windows 7 and Linux Mint.

![afl-editor-3](https://i.imgur.com/xp3jYAG.png)

![afl-editor-4](https://i.imgur.com/vX8Tl7U.png)

![afl-editor-5](https://i.imgur.com/kKNhz7I.png)![afl-editor-6](https://i.imgur.com/Q1TqU7h.png)

![afl-editor-7](https://i.imgur.com/fmpkXT9.png)![afl-editor-8](https://i.imgur.com/fw5K8tE.png)
## Example 4
Screenshots of the tool working on Windows XP, running straight from the JAR, and Command Prompt.
![afl-editor-9](https://i.imgur.com/zDNGmem.png)

![afl-editor-10](https://i.imgur.com/tCWRVA3.png)

![afl-editor-11](https://i.imgur.com/ZOCfMSl.png)

![afl-editor-12](https://i.imgur.com/PresINg.png)

![afl-editor-13](https://i.imgur.com/6gqkHSG.png)

![afl-editor-14](https://i.imgur.com/1RjLb6I.png)

![afl-editor-15](https://i.imgur.com/Ptkwr1a.png)

![afl-editor-16](https://i.imgur.com/pW9Hfzt.png)

![afl-editor-17](https://i.imgur.com/phW5WG0.png)

# Notes
1. The ``-ra`` and ``-rf`` refer to instances of the string to find and replace within **each file name**, not the whole AFL.

So, using ``-rf`` will not make one replacement for the whole AFL, but rather **only one replacement for each file name**.

2. In case it was not obvious, this tool was made purely because [HiroTex](https://github.com/HiroTex)'s take on an AFL editor (integrated into [Sparking Studio](https://mega.nz/file/OYM3TQrQ#Sq8_IoOpFI30vF4dDr0R0-JmpQbaseY0fQgsBGjiWxk)) is:
* written in **C#** (aka Java: Microsoft Edition), oh mama;
* only meant to be used on **Windows**, as running it through WINE on Linux apparently causes issues;
* able to edit **one AFL at a time**, whereas mine allows for several to be edited, as long as they are all in one folder.

The only upside is that **his tool has some GUI**. That's it. 

Fortunately, so **does mine** (as of 22 November 2024).
