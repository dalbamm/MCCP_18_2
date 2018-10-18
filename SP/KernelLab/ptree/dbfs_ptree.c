#include <linux/debugfs.h>
#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/uaccess.h>

MODULE_LICENSE("GPL");

static struct dentry *dir, *inputdir, *ptreedir;
static struct task_struct *curr;
typedef struct task_struct* ts;
typedef struct pid* pointer_pid;
pointer_pid g_pid;
static struct debugfs_blob_wrapper blobWrapper;
static void printProcessName(ts pTaskStruct, loff_t *position){
        static char chararr[10000];
        int cnt = 0;
        ts pids[1000];
        int i, len2 = strlen(chararr);
        ts curr = pTaskStruct;

        for(i=0 ; i < len2 ; ++i){
                                chararr[i] = 0;
                        }
     //   char* rst = k;
        while(1){
                char * tmp;
                char * tmp2;
                char enter = '\n';
                char *wk; int len=0;
                int idx = cnt++;
                printk("DBGs:::%d",curr);        
                printk("DBGid:::%d",curr->pid);
                printk("DBGcom:::%s", curr->comm);
                pids[idx]=curr;
                printk("DBG:::%d", pids[idx]->pid);
                printk("DBG:::%s", pids[idx]->comm);
                if(curr->pid==1){
                        len = 0;
                        printk("%d", cnt);
                        for(i = cnt-1 ; i >= 0 ; --i){
                                int j;
                                sprintf(chararr+len, "%s (%d)\n", pids[i]->comm, pids[i]->pid);
                                printk("%s,%d",pids[i]->comm, pids[i]->pid);
                                char* ptr =(char*)pids[i];
                                for(j = 0 ; j < sizeof(ts) ; ++j){
                                        *(ptr+j) = 0;
                                }
                                len = strlen(chararr);                           
                                printk("%d %d",len, strlen(chararr));
                                
                        }
                        blobWrapper.data = chararr;
                        blobWrapper.size = len;
                        ptreedir = debugfs_create_blob("ptree", 0644, dir, &blobWrapper); // Find suitable debugfs API
                        printk("%s", chararr);
                        
                        break;
                }
                curr = curr->parent;
        }
}
static ssize_t write_pid_to_input(struct file *fp, 
                                const char __user *user_buffer, 
                                size_t length, 
                                loff_t *position)
{
        pid_t input_pid;
        sscanf(user_buffer, "%u", &input_pid);
        g_pid = find_get_pid(input_pid);
        curr = pid_task(g_pid, PIDTYPE_PID);// Find task_struct using input_pid. Hint: pid_task
        printProcessName(curr,position);
        // Tracing process tree from input_pid to init(1) process
        // Make Output Format string: process_command (process_id)
        return length;
}

static const struct file_operations dbfs_fops = {
        .write = write_pid_to_input,
};

static int __init dbfs_module_init(void)
{
        // Implement init module code

        dir = debugfs_create_dir("ptree", NULL);
        
        if (!dir) {
                printk("Cannot create ptree dir\n");
                return -1;
        }
                 /* get mem for blob struct and init */
        inputdir = debugfs_create_file("input", 0644, dir, blobWrapper.data, &dbfs_fops);
        ptreedir = debugfs_create_blob("ptree", 0644, dir, &blobWrapper); // Find suitable debugfs API

        return 0;       
}

static void __exit dbfs_module_exit(void)
{
        // Implement exit module code
        debugfs_remove_recursive(dir);
}

module_init(dbfs_module_init);
module_exit(dbfs_module_exit);
