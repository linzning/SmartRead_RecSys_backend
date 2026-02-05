package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 主题关联关系实体（知识图谱）
 */
@Data
@TableName("topic_relations")
public class TopicRelation {
    
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 主题名称
     */
    private String topicName;
    
    /**
     * 关联主题名称
     */
    private String relatedTopicName;
    
    /**
     * 关联类型（related, parent, child等）
     */
    private String relationType;
    
    /**
     * 关联权重（0-1）
     */
    private Double weight;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

