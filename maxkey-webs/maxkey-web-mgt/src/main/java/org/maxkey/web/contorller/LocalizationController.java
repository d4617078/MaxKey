/*
 * Copyright [2022] [MaxKey of copyright http://www.maxkey.top]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 

package org.maxkey.web.contorller;

import org.apache.commons.lang3.StringUtils;
import org.maxkey.constants.ConstantsOperateMessage;
import org.maxkey.entity.Localization;
import org.maxkey.persistence.db.LocalizationService;
import org.maxkey.web.WebContext;
import org.maxkey.web.message.Message;
import org.maxkey.web.message.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value={"/localization"})
public class LocalizationController {


		final static Logger _logger = LoggerFactory.getLogger(LocalizationController.class);
		
		@Autowired
		private LocalizationService localizationService;
		
		/**
		 * 读取
		 * @return
		 */
		@RequestMapping(value={"/forward/{property}"})
		public ModelAndView forward(@PathVariable("property") String property){
			Localization localization = localizationService.get(property,WebContext.getUserInfo().getInstId());
			if(localization == null )localization = new Localization();
			localization.setProperty(property);
			localization.setInstId(WebContext.getUserInfo().getInstId());
			return new ModelAndView("localization/updateLocalization","model",localization);
		}
		
		/**
		 * 更新
		 * @param sysConfig
		 * @return
		 */
		@RequestMapping(value={"/update"})
		@ResponseBody
		public Message updat(@ModelAttribute("localization") Localization localization,BindingResult result) {
			_logger.debug("update  localization : "+localization);
			localization.setInstId(WebContext.getUserInfo().getInstId());
			if(StringUtils.isBlank(localization.getId())){
				localization.setId(localization.generateId());
				if(localizationService.insert(localization)) {
					return new Message(WebContext.getI18nValue(ConstantsOperateMessage.UPDATE_SUCCESS),MessageType.success);
				} else {
					return new Message(WebContext.getI18nValue(ConstantsOperateMessage.UPDATE_ERROR),MessageType.error);
				}
			}else {
				if(localizationService.update(localization)) {
					return new Message(WebContext.getI18nValue(ConstantsOperateMessage.UPDATE_SUCCESS),MessageType.success);
				} else {
					return new Message(WebContext.getI18nValue(ConstantsOperateMessage.UPDATE_ERROR),MessageType.error);
				}
			}
		}
		

}
