/*
 * Copyright (c) 2015 EDMI NZ
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of EDMI. 
 * ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with EDMI.
 */

/**
 * // TODO class Javadoc
 *
 * @author richard.parratt
 */
public interface Rule {

  Known solve(Prob wanted, int depth);
}
