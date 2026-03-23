// 批量生成新闻封面图 SVG
const fs = require('fs');
const path = require('path');

const OUT_DIR = path.join(__dirname, 'campus-news-frontend/public/covers');

// 分类配置
const CATEGORIES = {
  1: {
    name: '校园动态',
    gradient: ['#4f6ef7', '#7c5bf5'],
    accent: '#a855f7',
    icons: ['🏫', '🎓', '📢', '🌟', '✨', '🎊', '📋', '🔧', '🌿', '☕', '🚲', '🏗️']
  },
  2: {
    name: '通知公告',
    gradient: ['#f59e0b', '#ef4444'],
    accent: '#dc2626',
    icons: ['📢', '📝', '📋', '🔔', '⏰', '📌', '🗓️', '⚠️', '🛡️', '📚']
  },
  3: {
    name: '学术科研',
    gradient: ['#059669', '#0d9488'],
    accent: '#14b8a6',
    icons: ['🔬', '🧪', '📊', '💡', '🧬', '🤖', '🏆', '📖', '🎓', '⚗️', '🔭', '💻']
  },
  4: {
    name: '文体活动',
    gradient: ['#e11d48', '#f43f5e'],
    accent: '#fb7185',
    icons: ['⚽', '🏀', '🎤', '🎭', '🎵', '🏃', '📸', '🎬', '🛶', '🎮', '🏊', '🧗']
  },
  5: {
    name: '就业实习',
    gradient: ['#2563eb', '#3b82f6'],
    accent: '#60a5fa',
    icons: ['💼', '📝', '🎯', '🤝', '📊', '🏢', '🎓', '🌏', '💰', '📈', '🔑', '🪜']
  }
};

// 每条新闻的具体 icon（按文章顺序配对）
const NEWS_ICONS = {
  // 校园动态
  1: '🎨', 2: '🏗️', 3: '🏆', 4: '📋', 5: '🚲',
  6: '🔬', 7: '📡', 8: '🎂', 9: '💚', 10: '🌿',
  11: '💼', 12: '☕',
  16: '🤝', 17: '📦', 18: '🏅', 19: '📋', 20: '🚲',
  21: '🔬', 22: '📡', 23: '🎂', 24: '💚', 25: '🌿',
  26: '💼', 35: '☕',
  // 通知公告
  4: '📸', 27: '🛵', 28: '🏥', 29: '💡', 30: '🔍',
  31: '📚', 32: '❄️', 33: '🌿', 34: '🔐', 35: '🏆',
  // 学术科研
  36: '⚗️', 37: '🤖', 38: '📊', 39: '🏥', 40: '🎓',
  41: '📚', 42: '🌱', 43: '⚛️', 44: '💰', 45: '👨‍⚖️',
  46: '💻', 47: '📺',
  // 文体活动
  48: '🎭', 49: '🏃', 50: '🎤', 51: '📸', 52: '💃',
  53: '🎵', 54: '⚽', 55: '🎭', 56: '🎮', 57: '🎶',
  58: '🧗', 59: '🐉',
  // 就业实习
  60: '💻', 61: '📱', 62: '🏦', 63: '✏️', 64: '🌏',
  65: '🏗️', 66: '📝', 67: '🚀', 68: '📄', 69: '👮',
  70: '🤝', 71: '🏛️'
};

// 所有新闻数据
const NEWS_DATA = [
  // 校园动态 (cat 1) - 15篇
  { id: 1, cat: 1, title: '我校成功举办第十二届校园文化艺术节' },
  { id: 2, cat: 1, title: '新学期校园设施升级改造完成' },
  { id: 3, cat: 1, title: '我校获评全国文明校园荣誉称号' },
  { id: 16, cat: 1, title: '我校与海外三所知名高校签署合作协议' },
  { id: 17, cat: 1, title: '校园快递驿站升级改造完成' },
  { id: 18, cat: 1, title: '我校学生在全国大学生数学竞赛中获奖' },
  { id: 19, cat: 1, title: '我校召开2026年度工作会议' },
  { id: 20, cat: 1, title: '校园共享单车正式投放运营' },
  { id: 21, cat: 1, title: '我校获批建设省部级重点实验室' },
  { id: 22, cat: 1, title: '校园网全面升级至Wi-Fi 6' },
  { id: 23, cat: 1, title: '我校举办建校70周年校庆系列活动启动仪式' },
  { id: 24, cat: 1, title: '学校心理咨询服务全面升级' },
  { id: 25, cat: 1, title: '我校获评省级绿色学校示范单位' },
  { id: 26, cat: 1, title: '毕业季校园招聘会圆满收官' },
  { id: 35, cat: 1, title: '校园咖啡文化季开幕' },
  // 通知公告 (cat 2) - 10篇
  { id: 4, cat: 2, title: '关于2026年清明节放假安排的通知' },
  { id: 5, cat: 2, title: '图书馆关于延长开放时间的通知' },
  { id: 6, cat: 2, title: '关于开展2026年春季学期选课的通知' },
  { id: 27, cat: 2, title: '关于2026届毕业生图像信息采集的通知' },
  { id: 28, cat: 2, title: '关于加强校园电动车管理的通知' },
  { id: 29, cat: 2, title: '校医院关于春季流感预防的温馨提示' },
  { id: 30, cat: 2, title: '2026年大学生创新创业训练计划项目申报通知' },
  { id: 31, cat: 2, title: '关于开展校园安全检查的通知' },
  { id: 32, cat: 2, title: '图书馆数据库试用通知' },
  { id: 33, cat: 2, title: '关于调整部分教学楼空调开放时间的通知' },
  { id: 34, cat: 2, title: '关于2026年暑期社会实践活动立项申报的通知' },
  { id: 72, cat: 2, title: '校园网用户账号安全提醒' },
  { id: 73, cat: 2, title: '关于举办2026年挑战杯校内选拔赛的通知' },
  // 学术科研 (cat 3) - 12篇
  { id: 7, cat: 3, title: '我校计算机学院团队在国际顶级会议发表论文' },
  { id: 8, cat: 3, title: '2026年度国家自然科学基金申报指南发布' },
  { id: 9, cat: 3, title: '研究生学术论坛暨优秀论文评选活动通知' },
  { id: 36, cat: 3, title: '我校化学学院在Nature子刊发表重要研究成果' },
  { id: 37, cat: 3, title: '我校获批国家重点研发计划项目' },
  { id: 38, cat: 3, title: '经济学院举办数字经济与高质量发展学术研讨会' },
  { id: 39, cat: 3, title: '我校学者在国际权威医学期刊发表临床研究成果' },
  { id: 40, cat: 3, title: '第十二届全国博士生学术论坛在我校举办' },
  { id: 41, cat: 3, title: '我校新增3个博士学位授权一级学科' },
  { id: 42, cat: 3, title: '环境学院团队开发新型污水处理技术' },
  { id: 43, cat: 3, title: '诺贝尔物理学奖得主来校作学术报告' },
  { id: 44, cat: 3, title: '我校科研经费总额突破8亿元' },
  { id: 45, cat: 3, title: '法学院举办人工智能法律规制学术沙龙' },
  { id: 46, cat: 3, title: '我校团队获国际大学生程序设计竞赛亚洲区金牌' },
  { id: 47, cat: 3, title: '哲学系教授受邀在央视百家讲坛开讲' },
  // 文体活动 (cat 4) - 12篇
  { id: 10, cat: 4, title: '校篮球队勇夺省大学生联赛冠军' },
  { id: 11, cat: 4, title: '第十届校园之声歌手大赛报名开始' },
  { id: 12, cat: 4, title: '春季运动会定于4月中旬举办' },
  { id: 48, cat: 4, title: '我校话剧社原创话剧薪火首演成功' },
  { id: 49, cat: 4, title: '校园马拉松挑战赛完美收官' },
  { id: 50, cat: 4, title: '我校学生在省大学生辩论赛中夺冠' },
  { id: 51, cat: 4, title: '春季摄影大赛获奖作品展开幕' },
  { id: 52, cat: 4, title: '校舞蹈团赴香港参加国际大学生舞蹈节' },
  { id: 53, cat: 4, title: '第十届校园之声歌手大赛海选结束' },
  { id: 54, cat: 4, title: '我校承办省大学生足球联赛' },
  { id: 55, cat: 4, title: '经典话剧雷雨专场演出预告' },
  { id: 56, cat: 4, title: '我校龙舟队备战全国大学生龙舟赛' },
  { id: 57, cat: 4, title: '电竞社举办王者荣耀校园争霸赛' },
  { id: 58, cat: 4, title: '校合唱团参加省大学生艺术展演获一等奖' },
  { id: 59, cat: 4, title: '户外运动社组织春季登山活动' },
  // 就业实习 (cat 5) - 12篇
  { id: 13, cat: 5, title: '多家知名企业将于本周来校举办专场招聘会' },
  { id: 14, cat: 5, title: '关于开展2026年暑期实习生招募的通知' },
  { id: 15, cat: 5, title: '考公上岸经验分享会预告' },
  { id: 60, cat: 5, title: '互联网大厂2026暑期实习招聘全面启动' },
  { id: 61, cat: 5, title: '我校与华为签署人才培养合作协议' },
  { id: 62, cat: 5, title: '银行券商2026校园招聘笔试经验分享' },
  { id: 63, cat: 5, title: '教师资格证考试备考全攻略' },
  { id: 64, cat: 5, title: '留学申请经验交流会预告' },
  { id: 65, cat: 5, title: '央企2026年春季校园招聘行程公布' },
  { id: 66, cat: 5, title: '考研复试经验如何在面试中脱颖而出' },
  { id: 67, cat: 5, title: '创业学院举办互联网+创新创业训练营' },
  { id: 68, cat: 5, title: '简历优化工作坊让你的简历脱颖而出' },
  { id: 69, cat: 5, title: '我校15名学生入选西部计划志愿者' },
  { id: 70, cat: 5, title: '公务员面试培训营开放报名' },
  { id: 71, cat: 5, title: '我校校友企业专场招聘会成功举办' }
];

// 去重（有些id重复）
const seen = new Set();
const UNIQUE_NEWS = NEWS_DATA.filter(n => {
  if (seen.has(n.id)) return false;
  seen.add(n.id);
  return true;
});

// 生成 SVG
function generateSVG(news) {
  const cat = CATEGORIES[news.cat];
  const icon = NEWS_ICONS[news.id] || cat.icons[news.id % cat.icons.length];
  const [c1, c2] = cat.gradient;
  const gradId = `grad${news.id}`;
  const title = news.title;

  // 根据标题长度调整字号
  const fontSize = title.length > 20 ? (title.length > 28 ? 26 : 30) : 36;

  // 装饰元素 - 生成一些随机位置的圆圈
  const seed = news.id * 7 + 13;
  const circles = [];
  for (let i = 0; i < 6; i++) {
    const cx = ((seed * (i + 1) * 37) % 600) + 100;
    const cy = ((seed * (i + 1) * 53) % 200) + 50;
    const r = ((seed * (i + 1) * 17) % 40) + 15;
    circles.push(`<circle cx="${cx}" cy="${cy}" r="${r}" fill="white" opacity="0.06"/>`);
  }

  // 更多装饰 - 几何图形
  const decoX = ((seed * 31) % 300) + 500;
  const decoY = ((seed * 47) % 100) + 50;

  return `<svg xmlns="http://www.w3.org/2000/svg" width="800" height="400" viewBox="0 0 800 400">
  <defs>
    <linearGradient id="${gradId}" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:${c1}"/>
      <stop offset="100%" style="stop-color:${c2}"/>
    </linearGradient>
    <linearGradient id="${gradId}overlay" x1="0%" y1="100%" x2="0%" y2="0%">
      <stop offset="0%" style="stop-color:rgba(0,0,0,0.3)"/>
      <stop offset="100%" style="stop-color:rgba(0,0,0,0)"/>
    </linearGradient>
  </defs>

  <!-- 背景 -->
  <rect width="800" height="400" fill="url(#${gradId})"/>

  <!-- 装饰圆 -->
  ${circles.join('\n  ')}

  <!-- 大装饰圆 -->
  <circle cx="${decoX}" cy="${decoY}" r="120" fill="white" opacity="0.04"/>
  <circle cx="${decoX + 80}" cy="${decoY + 60}" r="80" fill="white" opacity="0.05"/>

  <!-- 右下装饰 -->
  <circle cx="750" cy="380" r="100" fill="${cat.accent}" opacity="0.15"/>
  <circle cx="680" cy="350" r="60" fill="white" opacity="0.06"/>

  <!-- 底部渐变遮罩 -->
  <rect y="200" width="800" height="200" fill="url(#${gradId}overlay)"/>

  <!-- 分类标签 -->
  <rect x="40" y="36" rx="16" ry="16" width="${cat.name.length * 16 + 48}" height="32" fill="rgba(255,255,255,0.2)"/>
  <text x="58" y="58" font-family="Arial, 'Microsoft YaHei', sans-serif" font-size="14" fill="white" font-weight="600">${icon} ${cat.name}</text>

  <!-- 中央大图标 -->
  <text x="680" y="140" font-size="80" text-anchor="middle" opacity="0.25">${icon}</text>

  <!-- 标题 -->
  <text x="40" y="300" font-family="Arial, 'Microsoft YaHei', sans-serif" font-size="${fontSize}" fill="white" font-weight="700">
    ${escapeXml(title.length > 16 ? title.substring(0, 16) + '...' : title)}
  </text>
  ${title.length > 16 ? `<text x="40" y="${300 + fontSize + 8}" font-family="Arial, 'Microsoft YaHei', sans-serif" font-size="${fontSize - 4}" fill="rgba(255,255,255,0.85)" font-weight="500">${escapeXml(title.length > 32 ? title.substring(16, 32) + '...' : title.substring(16))}</text>` : ''}

  <!-- 底部装饰线 -->
  <rect x="40" y="370" width="60" height="3" rx="1.5" fill="rgba(255,255,255,0.5)"/>

  <!-- 右下角品牌 -->
  <text x="760" y="388" font-family="Arial, 'Microsoft YaHei', sans-serif" font-size="11" fill="rgba(255,255,255,0.4)" text-anchor="end">📰 校园新闻</text>
</svg>`;
}

function escapeXml(str) {
  return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&apos;');
}

// 生成所有 SVG
let count = 0;
for (const news of UNIQUE_NEWS) {
  const svg = generateSVG(news);
  const filePath = path.join(OUT_DIR, `news-${news.id}.svg`);
  fs.writeFileSync(filePath, svg, 'utf-8');
  count++;
}

console.log(`Generated ${count} SVG files in ${OUT_DIR}`);
