package com.zhoukai.librarysystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("borrow_record")
/**
 * 借阅记录实体：保存一次借书从借出到归还的完整信息。
 *
 * 关键词：读者、图书、借出日期、应还日期、归还日期、状态。
 * 这张表是借阅业务的中心，既保存关联 ID，也保留姓名、电话和书名，
 * 这样列表查询时可以直接展示信息，不必每次都联表查询。
 */
public class BorrowRecord {
    /** 借阅记录主键，数据库自动递增。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联的图书 ID，对应 book_info.id。 */
    private Long bookId;

    /** 关联的读者 ID，对应 reader_info.id。 */
    private Long readerId;

    /** 借书时保存的读者姓名，用于记录展示和关键词搜索。 */
    private String readerName;

    /** 借书时保存的读者电话，后台登记时要求为 10 位数字。 */
    private String readerPhone;

    /** 借书时保存的书籍编号。 */
    private String bookIsbn;

    /** 借书时保存的书籍名称。 */
    private String bookName;

    /** 借出日期。 */
    private LocalDate borrowDate;

    /** 应还日期：不能早于借出日期，且最多为借出日期后的 6 个月。 */
    private LocalDate dueDate;

    /** 实际归还日期：未归还时为 null，归还时写入当天日期。 */
    private LocalDate returnDate;

    /**
     * 借阅状态：0=借阅中，1=已归还，2=逾期。
     * 状态决定页面显示的标签，也决定记录是否还能归还或续借。
     */
    private Integer status;

    /** 操作员 ID：记录是哪位管理员办理了借书；用户自助借书时可以为空。 */
    private Long operatorId;

    /** 备注：保存额外说明。 */
    private String remark;

    /** 记录创建时间。 */
    private LocalDateTime createTime;
}
