package com.xperia64.jusrcheat;

import com.xperia64.jusrcheat.R4Cheat.R4ProgressCallback;

public class CallbackTestClass implements R4ProgressCallback {
		@Override
		public void setProgress(int num, int max) {
			System.out.println(String.format("%d/%d",num,max));
		}
}
