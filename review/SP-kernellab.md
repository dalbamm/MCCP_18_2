## KernelLab

1. ## ptree

   - dbfs_ptree.c

   - Makefile

   - #### Task:

     - ##### <button>Process tree Tracing </button> :

       getpid() -> return process id

       getppid() -> return parent process id

       --> only kernel space has whole information.

       - ##### Then, how do we access <button>kernel space</button>?

         - <button>task_struct </button> contains whole information for the process 

           ##### ^- Need task_struct structure 

         - ##### Execution progress

           - build

             1.  <button>insmod</button> should be included in Makefile.

           - debugfs file name

             2. input the pid to /sys/kernel/debug/ptree/input

             3. cat /sys/kernel/debug/ptree/ptree.

                Example.

                ```c
                >make
                >sudo su
                >cd >/sys/kernel/debug/ptree
                >echo [pid ex.2881] >>input
                >cat ptree
                [result is printed]
                ```

         - ##### Related information

           1. kernel module convention

              The kernel developers have to follow the convention for Linux Kernel Module. See the source code below:

              ```
              1 #include <linux/module.h>
              2
              3 MODULE_LICENSE("GPL");
              4
              5 static int __init init_my_module(void)
              6 {
              7 // Running when this module is inserted to system
              8 }
              9
              10 static void __exit exit_my_module(void)
              11 {
              12 // Running when this module is removed from system
              13 }
              14
              15 module_init(init_my_module);
              16 module_exit(exit_my_module);
              ```

           This is a basic frame of code for Linux Kernel Module. There are two functions, the one is called when the kernel module is inserted to system and the other is called when the kernel module is removed from system. The two functions are enrolled to the kernel using module init and module exit functions. MOD-ULE LICENSE macro declares which license the module uses. (cited from the assignmnet spec hand out)

           2. Module load / unload commands in Linux

           ```
           load: root # insmod < module_name.ko >
           Unload: root # rmmod < module_name >
           Module list: devel $ lsmod
           ```

2. ### paddr

   - app.c
   - dvfs_paddr.c
   - Makefile

3. #### Report.pdf



### Tool: 

- Loadable Kernel Module 

- Debug File System. debugfs

- Linux Kenel Module Convention

- Debugfs APIs https://www.kernel.org/doc/Documentation/filesystems/debugfs.txt  
