#include <linux/debugfs.h>
#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/uaccess.h>
#include <asm/pgtable.h>

MODULE_LICENSE("GPL");

static struct dentry *dir, *output;
static struct task_struct *task;
static char* DATA;
struct packet{
        pid_t pid;
        unsigned long vaddr;
        unsigned long paddr;
};
pgd_t* mpgd;
pud_t* mpud;
pmd_t* mpmd;
pte_t* mpte;
unsigned long offset1;
unsigned long offset2;
unsigned long offset3;
unsigned long offset4;
unsigned long vadd;
unsigned long tmp;
struct mm_struct* mmm;
struct packet* pbuf;
static ssize_t read_output(struct file *fp,
                        char __user *user_buffer,
                        size_t length,
                        loff_t *position)
{
        // Implement read file operation
        pbuf=user_buffer;
        printk("%u",pbuf->pid);
        printk("%lx",pbuf->vaddr);
        printk("%d",pbuf->vaddr);
        printk("%lx",pbuf->paddr);
        task = pid_task( find_get_pid(pbuf->pid), PIDTYPE_PID );
        mmm=task->mm;//1st pagetable
        //printk("%d",mmm);
        mpgd=mmm->pgd;//2nd page
        vadd = pbuf->vaddr;
        offset1 = (pbuf->vaddr)>>30 & 3;
        offset2 = (pbuf->vaddr)>>21 & 511;
        offset3 = (pbuf->vaddr)>>12 & 511;
        offset4 = (pbuf->vaddr) & 4095;
        tmp = ((mpgd->pgd));
        printk("%d",mpgd);
        mpud=pud_offset(mpgd, vadd);
        printk("%d",mpud);
        
        mpmd=pmd_offset(mpud, vadd);
        printk("%d",mpmd);

        mpte=pte_offset_kernel(mpmd, vadd);
        printk("%u",mpte);

        printk("%d",((mpte->pte))<<12 | offset4 );
        printk("%lx",((mpte->pte)<<12) | offset4 );
        pbuf->paddr = ((mpte->pte)<<12) | offset4;
        printk("Hello");

}

static const struct file_operations dbfs_fops = {
        // Mapping file operations with your functions
                .read = read_output,

};

static int __init dbfs_module_init(void)
{
        // Implement init module

        dir = debugfs_create_dir("paddr", NULL);

        if (!dir) {
                printk("Cannot create paddr dir\n");
                return -1;
        }

        // Fill in the arguments below void* data
        output = debugfs_create_file("output", 0644, dir, NULL, &dbfs_fops);

        return 0;
}

static void __exit dbfs_module_exit(void)
{
        // Implement exit module
        debugfs_remove_recursive(dir);

}

module_init(dbfs_module_init);
module_exit(dbfs_module_exit);
