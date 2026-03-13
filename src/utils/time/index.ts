/**
 * 时间格式化工具函数
 */

/**
 * 格式化时间，去除T字符
 * @param time 时间字符串或Date对象
 * @param format 格式化模式，默认 'YYYY-MM-DD HH:mm:ss'
 * @returns 格式化后的时间字符串
 */
export function formatTime(time: string | Date | null | undefined, format: string = 'YYYY-MM-DD HH:mm:ss'): string {
  if (!time) return '-'  
  
  let date: Date  
  
  if (typeof time === 'string') {
    // 替换T为空格
    const timeStr = time.replace('T', ' ')
    
    // 处理 yyyy-MM-dd HH:mm:ss 格式
    const datetimeMatch = timeStr.match(/^(\d{4})-(\d{2})-(\d{2})\s+(\d{2}):(\d{2}):(\d{2})$/)
    if (datetimeMatch) {
      const [, year, month, day, hour, minute, second] = datetimeMatch.map(Number)
      date = new Date(year, month - 1, day, hour, minute, second)
    } else {
      // 处理其他格式
      date = new Date(timeStr)
    }
  } else {
    date = time
  }
  
  // 检查日期是否有效
  if (isNaN(date.getTime())) return '-'
  
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', String(year))
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化日期，只显示年月日
 * @param time 时间字符串或Date对象
 * @returns 格式化后的日期字符串
 */
export function formatDate(time: string | Date | null | undefined): string {
  return formatTime(time, 'YYYY-MM-DD')
}

/**
 * 格式化时间，只显示时分秒
 * @param time 时间字符串或Date对象
 * @returns 格式化后的时间字符串
 */
export function formatTimeOnly(time: string | Date | null | undefined): string {
  return formatTime(time, 'HH:mm:ss')
}
