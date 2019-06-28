package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecPararmMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecService {
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecPararmMapper specPararmMapper;
    public List<SpecGroup> querySpecGroups(Long id) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(id);

        List<SpecGroup> specGroups = this.specGroupMapper.select(specGroup);

        specGroups.forEach(s->{
            SpecParam specParam = new SpecParam();
            specParam.setGroupId(s.getId());//1
            List<SpecParam> specParams = specPararmMapper.select(specParam);
            s.setSpecParams(specParams);

        });
        return  specGroups;

    }

    public List<SpecParam> querySpecParam(Long gid, Long cid, Boolean searching, Boolean generic) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        specParam.setGeneric(generic);
        specPararmMapper.select(specParam);
        return    specPararmMapper.select(specParam); //select * from tb_spec_param  where cid=cid and searching=searching and  generic=generic

    }
}
