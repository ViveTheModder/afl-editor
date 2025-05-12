# afl-editor
A **Java** tool (supporting both CLI and GUI) that **finds & replaces strings in AFL files** (AFS File Lists).

That way, AFS Explorer can export these files independently, without them overwriting each other.
# Demonstration
## Example 1 - Find & Replace (Single AFL)
A **single AFL** taken from DBZ Budokai Tenkaichi 1. Here, I replace ``bgm`` with ``hello there``, which **contains a space**.

![afl-editor-1](https://github.com/user-attachments/assets/42ceeba6-f378-4bc3-8d3a-7b3cfabd35b8)
## Example 2 - Find & Replace (Multiple AFLs)
**Several AFLs** from DBZ Budokai Tenkaichi 3, both the final version and its prototype. 

Here, I replace ``Akuman`` with ``Vive_The_Modder``, and since **there are underscores**, **quotes and spaces are unnecessary**.

Also, ``PZS3US1_PROTO_OG.AFL`` is **skipped** because **its number of files does not properly line up** with the AFL's file size.
![afl-editor-2](https://github.com/user-attachments/assets/26a3a882-e1ab-4275-bb7b-343457f944f5)
## Example 3 - GUI Ver. on Win 7 & Linux Mint
Screenshots of (an old version of) the tool working on Windows 7 and Linux Mint.

![afl-editor-3](https://i.imgur.com/xp3jYAG.png)

![afl-editor-4](https://i.imgur.com/vX8Tl7U.png)

![afl-editor-5](https://i.imgur.com/kKNhz7I.png)![afl-editor-6](https://i.imgur.com/Q1TqU7h.png)

![afl-editor-7](https://i.imgur.com/fmpkXT9.png)![afl-editor-8](https://i.imgur.com/fw5K8tE.png)
## Example 4 - Fix Duplicate Names
The tool can also prevent duplicate file names by adding the file ID at the end of said duplicates.

![afl-editor-8](https://github.com/user-attachments/assets/7c371b14-98ba-4a1c-b91e-545f98fdf240)

![afl-editor-9](https://github.com/user-attachments/assets/9cf0f463-ee56-4374-abee-73c993c04c64)

![afl-editor-10](https://github.com/user-attachments/assets/ad89c395-8c1a-4051-8fc2-047c4b95be8c)

![afl-editor-11](https://github.com/user-attachments/assets/922060f4-0149-4f64-a81f-6ce263b3418d)

## Example 5 - Display/Edit Names
[AFS Explorer](http://www.mediafire.com/file/0xjq0c2g4r014iq/AFS_Explorer.zip) can already do this, but not for several AFLs...

![afl-editor-12](https://github.com/user-attachments/assets/95d0e38a-212f-4c70-8be0-1a7939976055)

![afl-editor-13](https://github.com/user-attachments/assets/62595dc6-4def-4a35-9ea1-f830c0405c5e)

![afl-editor-14](https://github.com/user-attachments/assets/3a4e27a7-a213-4e58-a8af-25ee60a6fb13)

![afl-editor-15](https://github.com/user-attachments/assets/35c54926-d33a-4cec-b949-21ab5600074b)

![afl-editor-16](https://github.com/user-attachments/assets/3c2676f5-19ca-4f43-a57c-a6c78bbcf862)

# Notes
1. The ``-ra`` and ``-rf`` refer to instances of the string to find and replace within **each file name**, not the whole AFL.

So, using ``-rf`` will not make one replacement for the whole AFL, but rather **only one replacement for each file name**.

2. In case it was not obvious, this tool was made purely because [HiroTex](https://github.com/HiroTex)'s take on an AFL editor (integrated into [Sparking Studio](https://mega.nz/file/OYM3TQrQ#Sq8_IoOpFI30vF4dDr0R0-JmpQbaseY0fQgsBGjiWxk)) is:
* written in **C#** (aka Java: Microsoft Edition), oh mama;
* only meant to be used on **Windows**, as running it through WINE on Linux apparently causes issues;
* able to edit **one AFL at a time**, whereas mine allows for several to be edited, as long as they are all in one folder.

The only upside is that **his tool has some GUI**. That's it. 

Fortunately, so **does mine** (as of 22 November 2024).
